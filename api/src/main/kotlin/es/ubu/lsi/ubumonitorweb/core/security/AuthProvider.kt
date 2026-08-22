package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.moodle.CredentialsClient
import es.ubu.lsi.ubumonitorweb.core.moodle.SiteInfoClient
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Proveedor de autenticación que provee objetos [Authentication].
 *
 * @author Marcelo Verteramo Pérsico
 */
@Component
@ImportHttpServices(CredentialsClient::class, SiteInfoClient::class)
class AuthProvider(
  private val credentialsClient: CredentialsClient,
  private val siteInfoClient: SiteInfoClient,
) : AuthenticationProvider {
  /** Indica el tipo de token soportado por este [AuthenticationProvider]. */
  override fun supports(authentication: Class<*>): Boolean =
    UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)

  /**
   * Realiza todo el procedimiento de autenticación necesario para obtener
   * las credenciales, el principal, y construir y devolver el AuthenticationToken.
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

    val principal =
      siteInfoClient.getPrincipal(
        credentials.token,
      )

    return object : AbstractAuthenticationToken(emptyList()) {
      init {
        super.isAuthenticated = true
      }

      override fun getCredentials() = credentials

      override fun getPrincipal() = principal
    }
  }
}
