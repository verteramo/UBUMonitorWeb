package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.security.authentication.AbstractAuthenticationToken

/**
 * Token con las credenciales y principal de Moodle.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
class MoodleAuthToken(
  private val credentials: MoodleCredentials,
  private val principal: MoodlePrincipal,
) : AbstractAuthenticationToken(emptyList()) {
  override fun getCredentials() = credentials

  override fun getPrincipal() = principal

  override fun isAuthenticated() = true
}
