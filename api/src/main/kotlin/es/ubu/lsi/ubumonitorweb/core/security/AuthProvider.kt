/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.moodle.SiteInfoClient
import es.ubu.lsi.ubumonitorweb.core.moodle.TokenClient
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Proveedor de autenticación que provee objetos [Authentication].
 */
@Component
@ImportHttpServices(TokenClient::class, SiteInfoClient::class)
class AuthProvider(
  private val tokenClient: TokenClient,
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
      tokenClient
        .getToken(authentication.name, authentication.credentials.toString())
        .toCredentials()

    val principal = siteInfoClient.getSiteInfo(credentials.token).toPrincipal()

    return object : AbstractAuthenticationToken(emptyList()) {
      init {
        super.isAuthenticated = true
      }

      override fun getCredentials() = credentials

      override fun getPrincipal() = principal
    }
  }
}
