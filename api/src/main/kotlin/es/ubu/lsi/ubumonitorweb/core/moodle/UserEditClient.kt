package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange

@ClientProfile
interface UserEditClient {
  @GetExchange
  fun getEditForm(
    @RequestHeader(HttpHeaders.COOKIE) cookie: String,
  ): ResponseEntity<String>
}
