//package es.ubu.lsi.ubumonitorweb.feature.users
//
//import com.fasterxml.jackson.annotation.JsonProperty
//import es.ubu.lsi.ubumonitorweb.core.base.MoodleRestService
//import org.springframework.web.bind.annotation.BindParam
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestParam
//import org.springframework.web.service.annotation.PostExchange
//
//data class FindParams(
//  val field: String,
//
//  @JsonProperty("values[0]") val value: String,
//)
//
//@MoodleRestService
//interface CoreUserService {
//
//  @PostExchange
//  fun getUsersByField(@RequestBody params: FindParams): Any
//}
