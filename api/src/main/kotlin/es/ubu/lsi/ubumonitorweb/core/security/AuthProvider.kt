/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.moodle.AuthService
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * Proveedor de autenticación que provee objetos [Authentication].
 */
@Component
class AuthProvider(
  private val authService: AuthService,
) : AuthenticationProvider {
  /**
   * Indica el tipo de token de Spring Security soportado por este [AuthenticationProvider].
   * No tiene nada que ver con el token de Moodle, es un método interno de Spring Security.
   */
  override fun supports(authentication: Class<*>): Boolean =
    UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)

  /**
   * Realiza el procedimiento de autenticación.
   */
  override fun authenticate(authentication: Authentication): Authentication? {
    // Obtención del usuario/contraseña desde el token de Spring Security
    val username = authentication.name
    val password = authentication.credentials.toString()

    val credentials = authService.getCredentials(username, password)
    val principal = authService.getPrincipal(credentials)

    // Construcción y retorno del objeto Authentication para Spring Security
    return object : AbstractAuthenticationToken(emptyList()) {
      init {
        super.isAuthenticated = true
      }

      override fun getCredentials() = credentials

      override fun getPrincipal() = principal
    }
  }
}
