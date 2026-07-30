package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
      @RequestBody credentials: MoodleCredentialsDto,
  ): ResponseEntity<MoodlePrincipalDto> {
    val authentication = manager.authenticate(
      UsernamePasswordAuthenticationToken(
        credentials.username, credentials.password,
      ),
    ) as MoodleAuthenticationToken

    val context = SecurityContextHolder.createEmptyContext()
    context.authentication = authentication
    SecurityContextHolder.setContext(context)
    repository.saveContext(context, request, response)

    return ResponseEntity.ok().body(authentication.principal)
  }

  @PostMapping("/principal")
  fun principal(
      @AuthenticationPrincipal principal: MoodlePrincipalDto,
  ): ResponseEntity<MoodlePrincipalDto> {
    return ResponseEntity.ok(principal)
  }
}
