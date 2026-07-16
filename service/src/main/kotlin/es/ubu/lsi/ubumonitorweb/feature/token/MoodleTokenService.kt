package es.ubu.lsi.ubumonitorweb.feature.token

import es.ubu.lsi.ubumonitorweb.core.rest.processor.DynamicHost
import es.ubu.lsi.ubumonitorweb.core.rest.processor.PropagateHeaders
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

data class MoodleToken(val token: String, val privatetoken: String)

@HttpExchange(
  url = $$"${exchange.auth-endpoint}",
  contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
)
@DynamicHost("Moodle-Host")
@PropagateHeaders(HttpHeaders.ACCEPT_LANGUAGE)
interface MoodleTokenService {

  @PostExchange
  fun getMoodleToken(@RequestBody credentials: Credentials): MoodleToken
}
