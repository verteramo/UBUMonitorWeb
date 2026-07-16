package es.ubu.lsi.ubumonitorweb.core.rest.converter

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper

/**
 * Convertidor de objetos a parámetros en formato
 * `application/x-www-form-urlencoded`.
 *
 * Spring Boot no tiene un [HttpMessageConverter] definido cuando en los
 * clientes [HttpExchange] se incluye como parámetro un objeto anotado con
 * [RequestBody], como por ejemplo:
 *
 * ```kotlin
 * data class UserData(val username: String, val password: String)
 *
 * @HttpExchange(contentType = "application/x-www-form-urlencoded")
 * interface ServicioRest {
 *   @PostMapping("/register")
 *   fun register(@RequestBody userData: UserData)
 * }
 * ```
 *
 * Lo esperado, utilizando este [MediaType], sería que se codificara el objeto
 * como: `username=value&password=value`. Hasta el momento se obtiene la
 * siguiente excepción: `RestClientException: No HttpMessageConverter for
 * UserData and content type "application/x-www-form-urlencoded"`
 *
 * Algo que probablemente se incluya en futuras versiones, puesto que es una
 * funcionalidad muy solicitada:
 * - https://github.com/spring-projects/spring-framework/issues/32142
 * - https://github.com/spring-projects/spring-framework/issues/35083
 * - https://github.com/spring-projects/spring-framework/issues/35381
 * - https://github.com/spring-projects/spring-framework/issues/35615
 * - https://github.com/spring-projects/spring-framework/issues/36246
 * - https://github.com/spring-projects/spring-framework/issues/36202
 *
 * Existe también una propuesta (rechazada):
 * - https://github.com/spring-projects/spring-framework/pull/35649
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class UrlEncodedHttpMessageConverter(
    private val mapper: ObjectMapper,
) : AbstractHttpMessageConverter<Any>(
  MediaType.APPLICATION_FORM_URLENCODED,
) {
  private val formConverter = FormHttpMessageConverter()

  override fun canRead(clazz: Class<*>, mediaType: MediaType?): Boolean =
      false

  override fun readInternal(
      clazz: Class<out Any>,
      inputMessage: HttpInputMessage,
  ): Any =
      throw UnsupportedOperationException()

  override fun supports(clazz: Class<*>): Boolean =
      !LinkedMultiValueMap::class.java.isAssignableFrom(clazz)

  override fun writeInternal(t: Any, outputMessage: HttpOutputMessage) {
    val typeReference = object : TypeReference<Map<String, Any>>() {}
    val map = LinkedMultiValueMap<String, Any>().apply {
      mapper.convertValue(t, typeReference).forEach(::add)
    }

    formConverter.write(
      map,
      MediaType.APPLICATION_FORM_URLENCODED,
      outputMessage,
    )
  }
}
