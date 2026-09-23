/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * Proveedor de autenticación para solicitudes con usuario/contraseña.
 */
@Component
class UsernamePasswordAuthenticationProvider(
  private val authService: AuthService,
) : AuthenticationProvider {
  override fun supports(authentication: Class<*>): Boolean =
    UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)

  override fun authenticate(authentication: Authentication): Authentication? {
    val username = authentication.name
    val password = authentication.credentials.toString()

    val credentials = authService.getCredentials(username, password)
    val principal = authService.getPrincipal(credentials)

    return UsernamePasswordAuthenticationToken(principal, credentials, emptyList())
  }
}
