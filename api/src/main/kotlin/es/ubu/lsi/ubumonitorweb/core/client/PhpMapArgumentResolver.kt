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
 * Resolver de argumentos HTTP que procesa colecciones anotadas con [PhpMap].
 *
 * Transforma mapas de colecciones en parámetros de consulta utilizando la sintaxis
 * de arrays asociativos anidados nativa de PHP. Los datos enviados se estructuran como:
 *
 * ```
 * paramName[mapKey][0]=itemValue&paramName[mapKey][1]=itemValue
 * ```
 */
@Component
class PhpMapArgumentResolver : HttpServiceArgumentResolver {
  /** Invocador del resolver. */
  override fun resolve(
    argument: Any?,
    parameter: MethodParameter,
    requestValues: HttpRequestValues.Builder,
  ): Boolean =
    parameter.getParameterAnnotation(PhpMap::class.java)?.let { annotation ->
      (argument as? Map<*, *>)?.run {
        val paramName = annotation.name.ifBlank { parameter.parameterName }

        forEach { (mapKey, list) ->
          if (mapKey != null && list is Collection<*>) {
            list.forEachIndexed { index, value ->
              if (value != null) {
                val itemKeyName = "$paramName[$mapKey][$index]"
                requestValues.addRequestParameter(itemKeyName, value.toString())
              }
            }
          }
        }

        true
      } ?: false
    } ?: false
}
