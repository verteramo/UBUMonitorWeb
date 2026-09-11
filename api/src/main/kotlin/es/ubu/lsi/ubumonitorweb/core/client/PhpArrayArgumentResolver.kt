/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver

/**
 * Resolver de argumentos HTTP que procesa colecciones anotadas con [PhpArray].
 *
 * Transforma listas de objetos en parámetros de consulta o de formulario
 * utilizando la sintaxis de arrays nativa de PHP. Los datos enviados
 * se estructuran como:
 *
 * ```
 * paramName[index]=value&...
 * ```
 */
@Component
class PhpArrayArgumentResolver : HttpServiceArgumentResolver {
  /**
   * Invocador del resolver.
   */
  override fun resolve(
    argument: Any?,
    parameter: MethodParameter,
    requestValues: HttpRequestValues.Builder,
  ): Boolean =
    parameter.getParameterAnnotation(PhpArray::class.java)?.let { array ->
      when (argument) {
        is List<*> -> argument
        else -> null
      }?.run {
        val paramName = array.name.ifBlank { parameter.parameterName }

        forEachIndexed { index, value ->
          val stringValue = value?.toString()

          if (!stringValue.isNullOrBlank()) {
            requestValues.addRequestParameter("$paramName[$index]", stringValue)
          }
        }

        true
      }
    } ?: false
}
