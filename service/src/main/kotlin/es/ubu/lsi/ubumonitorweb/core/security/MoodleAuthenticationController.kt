package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class MoodleAuthenticationController(
  private val manager: AuthenticationManager,
) {
  private val repository = HttpSessionSecurityContextRepository()

  @PostMapping("/login")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestParam username: String,
    @RequestParam password: String,
  ): MoodlePrincipalDto {
    val authentication =
      manager.authenticate(
        UsernamePasswordAuthenticationToken(username, password),
      ) as MoodleAuthenticationToken

    val context = SecurityContextHolder.createEmptyContext()
    context.authentication = authentication
    SecurityContextHolder.setContext(context)
    repository.saveContext(context, request, response)

    return authentication.principal
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun logout(
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication != null) {
      SecurityContextLogoutHandler().logout(request, response, authentication)
    }
  }

  @PostMapping("/principal")
  fun check(
    @AuthenticationPrincipal principal: MoodlePrincipalDto,
  ) = principal
}
