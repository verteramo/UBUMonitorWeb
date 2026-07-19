package es.ubu.lsi.ubumonitorweb.feature.token

import es.ubu.lsi.ubumonitorweb.core.security.JwtService
import io.github.oshai.kotlinlogging.KotlinLogging
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

  private val logger = KotlinLogging.logger {}

  @PostMapping
  fun getToken(@RequestBody credentials: Credentials): MoodleToken {
    val token = tokenService.getToken(credentials)
    return token
  }
}
