package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.service.ServiceProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ServiceProfile("principal")
interface MoodlePrincipalService {

  @PostExchange
  fun getPrincipal(
      @RequestParam wstoken: String,
  ): MoodlePrincipalDto
}
