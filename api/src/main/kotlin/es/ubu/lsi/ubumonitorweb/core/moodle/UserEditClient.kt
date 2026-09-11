package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.http.ResponseEntity
import org.springframework.web.service.annotation.GetExchange

@ClientProfile
interface UserEditClient {
  @GetExchange
  fun getEditForm(): ResponseEntity<String>
}
