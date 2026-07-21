package es.ubu.lsi.ubumonitorweb.feature.token

import es.ubu.lsi.ubumonitorweb.core.http.ServiceProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

data class MoodleToken(val token: String, val privatetoken: String) {
  override fun toString(): String {
    return token
  }
}

@ServiceProfile("auth")
interface TokenService {

  @PostExchange
  fun getToken(@RequestParam credentials: Credentials): MoodleToken
}
