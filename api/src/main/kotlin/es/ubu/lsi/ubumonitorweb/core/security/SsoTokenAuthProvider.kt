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
 * @property coreWebserviceClient Cliente API de Moodle.
 * @property loginClient Cliente web de Moodle.
 * @property userAssembler Ensamblador de sesión.
 */
@Component
@ImportHttpServices(CoreWebserviceClient::class, LoginClient::class)
class SsoTokenAuthProvider(
  private val cookieService: CookieService,
  private val coreWebserviceClient: CoreWebserviceClient,
  private val loginClient: LoginClient,
  private val userAssembler: UserAssembler,
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

    val moodleSiteInfo = coreWebserviceClient.getSiteInfo(ssoToken.token)

    cookieService.getOrFetchCookie(ssoToken, moodleSiteInfo.userid)

    val sessionKey = loginClient.getUserEditForm().sessionKey

    if (sessionKey.isNullOrBlank()) {
      throw AuthenticationServiceException("sessionKey (sesskey) is not available")
    }

    /*
     * Montaje del keychain para el contexto de la sesión
     * y ensamblaje final de los datos del usuario.
     */
    val keychain =
      Keychain(
        token = ssoToken.token,
        privateToken = ssoToken.privateToken,
        sessionKey = sessionKey,
      )

    val user = userAssembler.assemble(keychain.token) { moodleSiteInfo }

    return PreAuthenticatedAuthenticationToken(user, keychain, emptyList())
  }
}
