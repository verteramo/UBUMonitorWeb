package es.ubu.lsi.ubumonitorweb.feature.user.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpCollection
import es.ubu.lsi.ubumonitorweb.feature.user.dto.MoodleUser
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ClientProfile("webservice-client")
interface CoreEnrolClient {
  @PostExchange
  fun getEnrolledUsers(
    @RequestParam courseid: Int,
    @PhpCollection(keyName = "name") options: List<Pair<String, Any>> = emptyList(),
  ): List<MoodleUser>
}
