package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpArray
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleUser
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ClientProfile("webservice")
interface CoreUserClient {
  @PostExchange
  fun getUsersByField(
    @RequestParam field: String,
    @PhpArray values: List<String>,
  ): List<MoodleUser>
}
