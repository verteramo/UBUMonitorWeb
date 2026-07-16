package es.ubu.lsi.ubumonitorweb.core.rest.processor

import es.ubu.lsi.ubumonitorweb.core.base.ErrorResponse
import es.ubu.lsi.ubumonitorweb.core.base.annotation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilderFactory
import org.springframework.web.util.UriComponentsBuilder
import java.lang.reflect.Method
import java.net.URI
import java.net.URISyntaxException

/**
 * Anotación que permite usar host dinámico en servicios [HttpExchange], se
 * construye la factoría con el host que se obtiene desde el header
 * especificado. Por ejemplo:
 *
 * ```kotlin
 * @HttpExchange
 * @DynamicHost("X-Host")
 * interface ServicioRest {
 *   @GetExchange("/users-endpoint")
 *   fun getUsers()
 * }
 * ```
 *
 * Si el header tuviera un valor como el siguiente:
 *
 * ```http
 * X-Host: https://sandbox.moodledemo.net/
 * ```
 *
 * La solicitud final tendría el siguiente aspecto:
 *
 * ```http
 * GET https://sandbox.moodledemo.net/users-endpoint
 * ```
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class DynamicHost(val value: String)

/**
 * Procesador de la anotación que extrae el host del header de la solicitud
 * entrante y lo inyecta en un [UriBuilderFactory] preparado para construir el
 * template de la URI final de la solicitud.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class DynamicHostProcessor(private val request: HttpServletRequest) :
    HttpRequestValues.Processor {

  override fun process(
      method: Method,
      parameters: Array<out MethodParameter>,
      arguments: Array<out Any?>,
      requestValues: HttpRequestValues.Builder,
  ) { // Valor de la anotación:
    // Nombre del header que contiene el host
    method.annotation<DynamicHost>()?.value?.also { name ->

      // Obtención del valor del header
      val value = request.getHeader(name) ?:

      // El header no está presente en la solicitud
      throw ErrorResponse.HTTP_MISSING_HEADER(name)

      // El header está presente, pero está vacío
      if (value.isBlank()) throw ErrorResponse.HTTP_BLANK_HEADER(name)

      // Construcción de la URI
      val uri = try {
        URI(value)
      }

      // El valor del header no es válido (url malformada)
      catch (e: URISyntaxException) {
        throw ErrorResponse.HTTP_INVALID_HEADER(e, name, value)
      }

      // Construcción del UriBuilderFactory con el host entrante
      requestValues.setUriBuilderFactory(
        DefaultUriBuilderFactory(
          UriComponentsBuilder.fromUri(uri),
        ),
      )
    }
  }
}
