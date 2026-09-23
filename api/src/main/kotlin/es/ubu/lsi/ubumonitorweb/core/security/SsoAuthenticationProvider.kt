package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * Proveedor de autenticación para solicitudes con token SSO.
 */
@Component
class SsoAuthenticationProvider(
  private val authService: AuthService,
) : AuthenticationProvider {
  override fun supports(authentication: Class<*>): Boolean = SsoAuthenticationToken::class.java.isAssignableFrom(authentication)

  override fun authenticate(authentication: Authentication): Authentication? {
    val token = (authentication as SsoAuthenticationToken).token!!

    val credentials = authService.getCredentials(token)
    val principal = authService.getPrincipal(credentials)

    return SsoAuthenticationToken(principal, credentials)
  }
}
