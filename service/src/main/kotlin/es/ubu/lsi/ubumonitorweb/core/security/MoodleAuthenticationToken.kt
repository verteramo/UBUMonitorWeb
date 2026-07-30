package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class MoodleAuthenticationToken(
    private val token: MoodleTokenDto,
    private val principal: MoodlePrincipalDto,
) : AbstractAuthenticationToken(emptyList()) {
  override fun getPrincipal() = principal
  override fun getCredentials() = token
  override fun isAuthenticated() = true
}
