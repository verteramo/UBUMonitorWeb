package es.ubu.lsi.ubumonitorweb.feature.token

import es.ubu.lsi.ubumonitorweb.core.rest.MoodleService
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

data class MoodleToken(val token: String, val privatetoken: String)

@MoodleService("auth")
interface TokenService {

  @PostExchange
  fun getToken(
      @RequestParam username: String,
      @RequestParam password: String,
  ): MoodleToken
}
