package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.Credentials
import es.ubu.lsi.ubumonitorweb.domain.Principal
import org.springframework.security.authentication.AbstractAuthenticationToken

class SsoAuthenticationToken : AbstractAuthenticationToken {
  val token: String?
  private val principal: Principal?
  private val credentials: Credentials?

  constructor(token: String) : super(null) {
    this.token = token
    this.principal = null
    this.credentials = null
    super.isAuthenticated = false
  }

  constructor(principal: Principal, credentials: Credentials) : super(emptyList()) {
    this.token = null
    this.principal = principal
    this.credentials = credentials
    super.isAuthenticated = true
  }

  override fun getPrincipal() = principal

  override fun getCredentials() = credentials
}
