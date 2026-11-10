package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.Keyring
import es.ubu.lsi.ubumonitorweb.domain.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Service

@Service
class SecurityContextService(
  private val securityContextRepository: SecurityContextRepository,
) {
  private val securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()
  private val logoutHandler = SecurityContextLogoutHandler()

  private fun getAuthentication(
    user: User,
    keyring: Keyring,
  ): Authentication =
    object : AbstractAuthenticationToken(emptyList()) {
      override fun getPrincipal() = user

      override fun getCredentials() = keyring

      override fun isAuthenticated() = true
    }

  fun create(
    user: User,
    keyring: Keyring,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) = securityContextHolderStrategy
    .createEmptyContext()
    .apply { authentication = getAuthentication(user, keyring) }
    .apply { securityContextHolderStrategy.context = this }
    .run { securityContextRepository.saveContext(this, request, response) }

  fun destroy(
    authentication: Authentication,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) {
    logoutHandler.logout(request, response, authentication)
  }
}
