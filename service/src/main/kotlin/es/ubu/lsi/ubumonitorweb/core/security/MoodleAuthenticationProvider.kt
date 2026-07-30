package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.exception.MoodleException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

@Component
@ImportHttpServices(MoodleTokenService::class, MoodlePrincipalService::class)
class MoodleAuthenticationProvider(
    private val tokenService: MoodleTokenService,
    private val principalService: MoodlePrincipalService,
) : AuthenticationProvider {

  override fun supports(authentication: Class<*>): Boolean {
    return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
  }

  override fun authenticate(authentication: Authentication): Authentication? {
    val token = try {
      tokenService.getToken(authentication.name, authentication.credentials.toString())
    }
    catch (e: MoodleException) {
      throw BadCredentialsException(e.message, e)
    }

    val principal = principalService.getPrincipal(token.token)

    return MoodleAuthenticationToken(token, principal)
  }
}
