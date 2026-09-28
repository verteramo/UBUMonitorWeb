/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.resolver

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver

/**
 * Resolver de argumentos HTTP que serializa mapas de colecciones en arrays multidimensionales de PHP.
 *
 * @see PhpMultiValueMap
 */
@Component
class PhpMultiValueMapArgumentResolver : HttpServiceArgumentResolver {
  /**
   * Resuelve y añade las colecciones agrupadas por clave a la petición HTTP.
   *
   * @param argument Mapa con colecciones de valores recibido como argumento.
   * @param parameter Metadatos del parámetro interceptado.
   * @param requestValues Constructor mutable de la petición HTTP.
   * @return `true` si el argumento fue procesado, `false` en caso contrario.
   */
  override fun resolve(
    argument: Any?,
    parameter: MethodParameter,
    requestValues: HttpRequestValues.Builder,
  ): Boolean =
    parameter
      .getParameterAnnotation(PhpMultiValueMap::class.java)
      ?.run { name.ifBlank { parameter.parameterName } }
      ?.let { name ->
        require(argument is Map<*, *>) {
          "Parameter '$parameter' annotated with @PhpMultiValueMap must be a MultiValueMap or a map of collections."
        }

        argument.forEach { (mapKey, list) ->
          val mapKeyString = mapKey?.toString()

          if (mapKeyString?.isNotBlank() == true && list is Collection<*>) {
            list.forEachIndexed { index, value ->
              if (value != null) {
                val itemKeyName = "$name[$mapKeyString][$index]"
                requestValues.addRequestParameter(itemKeyName, value.toString())
              }
            }
          }
        }

        true
      } ?: false
}
