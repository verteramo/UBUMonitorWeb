package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.exception.MoodleException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices

@Component
@ImportHttpServices(MoodleCredentialsService::class, MoodlePrincipalService::class)
class MoodleAuthenticationProvider(
    private val credentialsService: MoodleCredentialsService,
    private val principalService: MoodlePrincipalService,
) : AuthenticationProvider {

  override fun supports(authentication: Class<*>): Boolean {
    return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
  }

  override fun authenticate(authentication: Authentication): Authentication? {
    val credentials = try {
      credentialsService.getToken(
        authentication.name, authentication.credentials.toString(),
      )
    }
    catch (e: MoodleException) {
      throw BadCredentialsException(e.message, e)
    }

    val principal = principalService.getPrincipal(credentials.token)

    return MoodleAuthenticationToken(credentials, principal)
  }
}
