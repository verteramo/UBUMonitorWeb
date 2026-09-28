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
 * Resolver de argumentos HTTP que serializa mapas y pares asociativos en arrays indexados de PHP.
 *
 * @see PhpMap
 */
@Component
class PhpMapArgumentResolver : HttpServiceArgumentResolver {
  /**
   * Resuelve y añade las entradas asociativas a la petición HTTP.
   *
   * @param argument Mapa o colección de pares recibido como argumento.
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
      .getParameterAnnotation(PhpMap::class.java)
      ?.also {
        require(argument is Map<*, *> || argument is Collection<*>) {
          "Parameter '$parameter' annotated with @PhpMap must be a map or a collection of pairs."
        }
      }?.run {
        when (argument) {
          is Map<*, *> -> argument.toList()
          is Collection<*> -> argument.filterIsInstance<Pair<*, *>>()
          else -> null
        }?.let {
          val paramName = name.ifBlank { parameter.parameterName }

          it.forEachIndexed { index, (key, value) ->
            val itemKey = key?.toString()
            val itemValue = value?.toString()

            if (!itemKey.isNullOrBlank() && !itemValue.isNullOrBlank()) {
              val itemKeyName = "$paramName[$index][$keyName]"
              val itemValueName = "$paramName[$index][$valueName]"

              requestValues.addRequestParameter(itemKeyName, itemKey)
              requestValues.addRequestParameter(itemValueName, itemValue)
            }
          }

          true
        }
      } ?: false
}
