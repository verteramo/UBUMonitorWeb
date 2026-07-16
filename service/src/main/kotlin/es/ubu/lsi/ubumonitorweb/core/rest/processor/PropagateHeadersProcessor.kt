package es.ubu.lsi.ubumonitorweb.core.rest.processor

import es.ubu.lsi.ubumonitorweb.core.base.annotation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import java.lang.reflect.Method
import java.util.*

/**
 * Anotación que permite especificar headers que se desean propagar desde la
 * solicitud entrante hacia la solicitud saliente de un servicio [HttpExchange].
 * Por ejemplo:
 *
 * ```kotlin
 * @HttpExchange
 * @PropagateHeaders("Accept-Language", "X-Custom-Header", ...)
 * interface ServicioRest {...}
 * ```
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class PropagateHeaders(vararg val value: String)

/**
 * Procesador de la anotación que obtiene los headers especificados y los
 * transfiere a la solicitud saliente.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class PropagateHeadersProcessor(private val request: HttpServletRequest) :
    HttpRequestValues.Processor {

  override fun process(
      method: Method,
      parameters: Array<out MethodParameter>,
      arguments: Array<out Any?>,
      requestValues: HttpRequestValues.Builder,
  ) {

    // Valor de la anotación:
    // Lista de headers que se desean propagar
    method.annotation<PropagateHeaders>()?.value

        // Obtención del valor de los headers
        ?.associateWith(request::getHeader)

        // Filtrado de headers nulos
        ?.filterValues(Objects::nonNull)

        // Filtrado de headers vacíos
        ?.filterValues(String::isNotBlank)

        // Propagación a la solicitud saliente
        ?.forEach(requestValues::addHeader)
  }
}
