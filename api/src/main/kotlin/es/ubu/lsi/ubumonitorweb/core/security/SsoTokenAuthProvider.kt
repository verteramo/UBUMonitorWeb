/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.sessionKey
import es.ubu.lsi.ubumonitorweb.domain.Keychain
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreWebserviceClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Proveedor de autenticación SSO para validar tokens pre-cargados desde el frontend.
 *
 * @property cookieService Servicio para la gestión de cookies de sesión.
 * @property loginClient Cliente web de Moodle.
 * @property coreWebserviceClient Cliente API de Moodle.
 */
@Component
@ImportHttpServices(CoreWebserviceClient::class, LoginClient::class)
class SsoTokenAuthProvider(
  private val cookieService: CookieService,
  private val loginClient: LoginClient,
  private val coreWebserviceClient: CoreWebserviceClient,
) : AuthenticationProvider {
  private val logger = KotlinLogging.logger {}

  /**
   * Comprueba si el proveedor soporta el token indicado.
   *
   * @param authentication Clase del token a evaluar.
   * @return `true` si es compatible.
   */
  override fun supports(authentication: Class<*>): Boolean =
    PreAuthenticatedAuthenticationToken::class.java.isAssignableFrom(authentication)

  /**
   * Autentica al usuario contra Moodle y genera el contexto de sesión.
   *
   * @param authentication Token con la credencial SSO.
   * @return Objeto de autenticación con el contexto de sesión.
   */
  override fun authenticate(authentication: Authentication): Authentication {
    val ssoToken = authentication.principal as AuthController.SsoTokenLoginRequest

    logger.info { "Starting SSO authentication" }
    logger.debug { "SSO Token: '$ssoToken'" }

    val moodleToken = decodeSsoToken(ssoToken.token)

    val moodleSiteInfo by lazy { coreWebserviceClient.getSiteInfo(moodleToken.token) }

    cookieService.getOrFetchCookie(moodleToken) { moodleSiteInfo.userid }

    val sessionKey = loginClient.getUserEditForm().sessionKey

    if (sessionKey.isNullOrBlank()) {
      throw AuthenticationServiceException("sessionKey (sesskey) is not available")
    }

    /*
     * Montaje del keychain para el contexto de la sesión.
     */
    val keychain =
      Keychain(
        token = moodleToken.token,
        privateToken = moodleToken.privatetoken,
        sessionKey = sessionKey,
      )

    return PreAuthenticatedAuthenticationToken(moodleSiteInfo, keychain, emptyList())
  }

  /**
   * Decodifica el token cifrado recibido por el deep link y extrae el par de tokens de Moodle.
   * Formato esperado tras Base64: hash:::token:::privatetoken (o similar)
   */
  private fun decodeSsoToken(encodedToken: String): MoodleToken {
    try {
      val decodedBytes =
        java.util.Base64
          .getDecoder()
          .decode(encodedToken)
      val decodedString = String(decodedBytes, Charsets.UTF_8)
      val parts = decodedString.split(":::")

      if (parts.size < 2) {
        throw AuthenticationServiceException("Malformed SSO token structure")
      }

      // parts[0] -> hash de seguridad (verificado por el backend si procede)
      // parts[1] -> token principal
      // parts[2] -> privatetoken (opcional según versión de Moodle)
      val token = parts[1]
      val privateToken = if (parts.size > 2) parts[2] else ""

      return MoodleToken(token = token, privatetoken = privateToken)
    } catch (e: Exception) {
      logger.error(e) { "Failed to decode SSO token" }
      throw AuthenticationServiceException("Invalid SSO token encoding", e)
    }
  }
}
