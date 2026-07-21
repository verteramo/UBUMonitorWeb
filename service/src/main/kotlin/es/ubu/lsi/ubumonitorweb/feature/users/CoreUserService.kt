package es.ubu.lsi.ubumonitorweb.feature.users

import com.fasterxml.jackson.annotation.JsonProperty
import es.ubu.lsi.ubumonitorweb.core.http.ServiceProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

data class FindParams(
    val field: String,
    @RequestParam("values[0]") val value: String,
)

@ServiceProfile("rest")
interface CoreUserService {

  @PostExchange
  fun getUsersByField(@RequestParam params: FindParams): Any
}
