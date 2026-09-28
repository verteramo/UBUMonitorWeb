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
import java.net.URLDecoder
import kotlin.io.encoding.Base64

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
    val token = authentication.principal.toString()

    logger.info { "Starting SSO authentication" }
    logger.debug { "SSO Token: '$token'" }

    /*
     * Decodificación del token SSO:
     * URL decode --> Base64 decode --> <hash>:::<token>[:::<privatetoken>]
     */
    val moodleToken =
      token
        .let { URLDecoder.decode(it, Charsets.UTF_8) }
        .let { Base64.decode(it).decodeToString() }
        .split(":::")
        .run { MoodleToken(token = get(1), privatetoken = getOrNull(2) ?: "") }

    val moodleSiteInfo = coreWebserviceClient.getSiteInfo(moodleToken.token)

    cookieService.getOrFetchCookie(moodleToken, moodleSiteInfo.userid)

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
        token = moodleToken.token,
        privateToken = moodleToken.privatetoken,
        sessionKey = sessionKey,
      )

    val user = userAssembler.assemble(keychain.token) { moodleSiteInfo }

    return PreAuthenticatedAuthenticationToken(user, keychain, emptyList())
  }
}
