/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.moodle.client.CoreWebserviceClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import io.github.oshai.kotlinlogging.KotlinLogging
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
 */
@Component
@ImportHttpServices(LoginClient::class, CoreWebserviceClient::class)
class CredentialsAuthProvider(
  private val loginClient: LoginClient,
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
    logger.info { "Starting credentials authentication" }

    val username = authentication.name
    val password = authentication.credentials.toString()
    val moodleToken = loginClient.getToken(username, password)

    return UsernamePasswordAuthenticationToken("", moodleToken, emptyList())
  }
}
