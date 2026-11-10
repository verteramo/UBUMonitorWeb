/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.locale.I18nService
import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.domain.Passport
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException
import org.springframework.stereotype.Component
import java.net.URLDecoder
import kotlin.io.encoding.Base64

/**
 * Proveedor de autenticación SSO para validar tokens pre-cargados desde el frontend.
 */
@Component
class SsoTokenAuthProvider(
  private val i18n: I18nService,
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
    logger.info { "Starting SSO token authentication" }

    val passport =
      authentication.credentials as Passport?
        ?: throw PreAuthenticatedCredentialsNotFoundException(
          i18n(Message.ERROR_AUTH_PASSPORT_MISSING),
        )

    val token = authentication.principal as String

    val (signature, moodleToken) =
      token
        .let { URLDecoder.decode(it, Charsets.UTF_8) }
        .let { Base64.decode(it).decodeToString() } // <signature>:::<token>[:::<privateToken>]
        .split(":::")
        .run { Pair(get(0), MoodleToken(get(1), getOrNull(2))) }

    if (passport.signature != signature) {
      throw BadCredentialsException(
        i18n(Message.ERROR_AUTH_PASSPORT_INVALID),
      )
    }

    return PreAuthenticatedAuthenticationToken("", moodleToken, emptyList())
  }
}
