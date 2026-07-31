package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class MoodleAuthenticationToken(
    private val credentials: MoodleCredentialsDto,
    private val principal: MoodlePrincipalDto,
) : AbstractAuthenticationToken(emptyList()) {
  override fun getCredentials() = credentials
  override fun getPrincipal() = principal
  override fun isAuthenticated() = true
}
