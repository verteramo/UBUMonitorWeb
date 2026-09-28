/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.system.HttpExtensions.first
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.loginToken
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.select
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.sessionKey
import es.ubu.lsi.ubumonitorweb.domain.Keychain
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreWebserviceClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.hc.client5.http.cookie.CookieStore
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Proveedor de autenticación mediante credenciales para Moodle.
 *
 * @property loginClient Cliente web de Moodle.
 * @property coreWebserviceClient Cliente API de Moodle.
 * @property userAssembler Ensamblador de sesión.
 * @property cookieStore Almacén de cookies.
 */
@Component
@ImportHttpServices(LoginClient::class, CoreWebserviceClient::class)
class CredentialsAuthProvider(
  private val loginClient: LoginClient,
  private val coreWebserviceClient: CoreWebserviceClient,
  private val userAssembler: UserAssembler,
  private val cookieStore: CookieStore,
) : AuthenticationProvider {
  private val logger = KotlinLogging.logger {}

  /**
   * Comprueba si el proveedor soporta el token indicado.
   *
   * @param authentication Clase del token a evaluar.
   * @return `true` si es compatible.
   */
  override fun supports(authentication: Class<*>): Boolean =
    UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)

  /**
   * Autentica al usuario contra Moodle y genera el contexto de sesión.
   *
   * @param authentication Token con las credenciales.
   * @return Objeto de autenticación con el contexto de sesión.
   *
   * @throws AuthenticationServiceException Si faltan elementos como el `logintoken` o la `sesskey`.
   * @throws BadCredentialsException Si las credenciales son inválidas.
   */
  override fun authenticate(authentication: Authentication): Authentication? {
    val username = authentication.name
    val password = authentication.credentials.toString()

    logger.info { "Starting username/password authentication" }
    logger.debug { "Credentials: '$username:$password'" }
    cookieStore.clear()

    /*
     * Inicio de sesión en el frontend de Moodle.
     *
     * 1. GET /login/index.php
     *    (extraer cookie, logintoken y sesskey)
     *
     * 2. POST /login/index.php
     *    Cookie: MoodleSession...: {...}
     *
     *    username={...}&password={...}&logintoken={...}
     */

    logger.info { "Calling GET /login/index.php" }

    val (loginToken, sessionKey) = loginClient.getIndex().run { Pair(loginToken, sessionKey) }

    if (loginToken.isNullOrBlank()) {
      throw AuthenticationServiceException("Login token (logintoken) is not available.")
    }

    if (sessionKey.isNullOrBlank()) {
      throw AuthenticationServiceException("Session key (sesskey) is not available")
    }

    logger.debug { "Login token: '$loginToken' | Session key: '$sessionKey'" }
    logger.info { "Calling POST /login/index.php" }

    if (loginClient.postIndex(username, password, loginToken).select("body#page-my-index").isEmpty()) {
      throw BadCredentialsException("Invalid credentials")
    }

    logger.debug { "Session cookie: '${cookieStore.first("MoodleSession")}'" }

    /*
     * Inicio de sesión en el backend de Moodle.
     */

    logger.info { "Calling POST /login/token.php" }

    val moodleToken = loginClient.getToken(username, password)

    /*
     * Montaje del keychain para el contexto de la sesión
     * y ensamblaje final de los datos del usuario.
     */
    val keychain =
      Keychain(token = moodleToken.token, privateToken = moodleToken.privatetoken, sessionKey = sessionKey)

    val user =
      userAssembler.assemble(keychain.token) { coreWebserviceClient.getSiteInfo(moodleToken.token) }

    logger.debug { "User: '$user'" }
    logger.debug { "Keychain: '$keychain'" }
    logger.info { "Logged in" }

    return UsernamePasswordAuthenticationToken(user, keychain, emptyList())
  }
}
