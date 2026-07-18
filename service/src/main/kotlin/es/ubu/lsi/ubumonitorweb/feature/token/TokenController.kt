package es.ubu.lsi.ubumonitorweb.feature.token

import es.ubu.lsi.ubumonitorweb.core.security.JwtService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Credentials(val username: String, val password: String)

@RestController
@RequestMapping("/api/token")
class TokenController(
    private val jwtService: JwtService,
    private val tokenService: TokenService,
) {

  @PostMapping
  fun getToken(@RequestBody credentials: Credentials): MoodleToken {
    return tokenService.getToken(
      credentials.username,
      credentials.password,
    )
  }
}
