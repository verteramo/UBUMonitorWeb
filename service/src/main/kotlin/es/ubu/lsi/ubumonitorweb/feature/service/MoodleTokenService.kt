package es.ubu.lsi.ubumonitorweb.feature.service

import es.ubu.lsi.ubumonitorweb.core.service.ServiceProfile
import es.ubu.lsi.ubumonitorweb.feature.controller.Credentials
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

data class MoodleToken(val token: String, val privatetoken: String) {
  override fun toString(): String = token
}

@ServiceProfile("auth")
interface MoodleTokenService {

  @PostExchange
  fun getToken(@RequestParam credentials: Credentials): MoodleToken
}
