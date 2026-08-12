package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Proveedor de autenticación que provee objetos [Authentication].
 *
 * @param credentialsClient Cliente HTTP que obtiene las credenciales.
 * @param principalClient Cliente HTTTP que obtiene los datos del usuario autenticado.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@ImportHttpServices(MoodleCredentialsClient::class, MoodlePrincipalClient::class)
class MoodleAuthProvider(
  private val credentialsClient: MoodleCredentialsClient,
  private val principalClient: MoodlePrincipalClient,
) : AuthenticationProvider {
  /** Indica el tipo de token soportado por este [AuthenticationProvider]. */
  override fun supports(authentication: Class<*>): Boolean =
    UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)

  /**
   * Realiza todo el procedimiento de autenticación necesario para obtener
   * las credenciales, el principal, y construir y devolver el Authentication Token.
   *
   * @param authentication Token sin autenticar.
   * @return Token autenticado.
   */
  override fun authenticate(authentication: Authentication): Authentication? {
    val credentials =
      credentialsClient.getCredentials(
        authentication.name,
        authentication.credentials.toString(),
      )

    val principal = principalClient.getPrincipal(credentials.token)

    return MoodleAuthToken(credentials, principal)
  }
}
