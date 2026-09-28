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
 * Resolver de argumentos HTTP que serializa colecciones en parámetros indexados de PHP.
 *
 * @see PhpCollection
 */
@Component
class PhpCollectionArgumentResolver : HttpServiceArgumentResolver {
  /**
   * Resuelve y añade los elementos de la colección a la petición HTTP.
   *
   * @param argument Valor de la colección recibido como argumento.
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
      .getParameterAnnotation(PhpCollection::class.java)
      ?.run { name.ifBlank { parameter.parameterName } }
      ?.let { name ->
        require(argument is Collection<*>) {
          "Parameter '$parameter' annotated with @PhpCollection must be a Collection."
        }

        argument.forEachIndexed { index, value ->
          val stringValue = value?.toString()

          if (!stringValue.isNullOrBlank()) {
            requestValues.addRequestParameter("$name[$index]", stringValue)
          }
        }

        true
      } ?: false
}
