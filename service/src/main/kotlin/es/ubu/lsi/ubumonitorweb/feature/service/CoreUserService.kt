package es.ubu.lsi.ubumonitorweb.feature.service

import es.ubu.lsi.ubumonitorweb.core.service.ServiceProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

data class FindParams(
  val field: String,
  @RequestParam("values[0]") val value: String,
)

@ServiceProfile("webservice")
interface CoreUserService {
  @PostExchange
  fun getUsersByField(
    @RequestParam wstoken: String,
    @RequestParam params: FindParams,
  ): Any

  @PostExchange
  fun getUsersByField(
    @RequestParam params: FindParams,
  ): Any
}
