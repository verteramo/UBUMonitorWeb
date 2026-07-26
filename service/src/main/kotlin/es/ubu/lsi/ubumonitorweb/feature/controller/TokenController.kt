package es.ubu.lsi.ubumonitorweb.feature.controller

import es.ubu.lsi.ubumonitorweb.core.security.AuthResponder
import es.ubu.lsi.ubumonitorweb.feature.service.MoodleToken
import es.ubu.lsi.ubumonitorweb.feature.service.MoodleTokenService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Credentials(val username: String, val password: String)

@RestController
@RequestMapping("/api/token")
class TokenController(
    private val moodleTokenService: MoodleTokenService,
    private val authResponder: AuthResponder<MoodleToken>,
) {

  @PostMapping
  fun getToken(
      response: HttpServletResponse,
      @RequestBody credentials: Credentials,
  ): Any? {
    val moodleToken = moodleTokenService.getToken(credentials)
    return authResponder.respond(response, moodleToken)
  }
}
