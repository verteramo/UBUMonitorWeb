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
 * Resolver de argumentos HTTP que procesa colecciones anotadas con [PhpCollection].
 *
 * Transforma listas de objetos en parámetros de consulta o de formulario
 * utilizando la sintaxis de arrays indexados nativa de PHP. Los datos enviados
 * se estructuran como:
 *
 * ```
 * paramName[index][keyName]=keyValue&paramName[index][valueName]=itemValue
 * ```
 */
@Component
class PhpCollectionArgumentResolver : HttpServiceArgumentResolver {
  /** Invocador del resolver. */
  override fun resolve(
    argument: Any?,
    parameter: MethodParameter,
    requestValues: HttpRequestValues.Builder,
  ): Boolean =
    parameter.getParameterAnnotation(PhpCollection::class.java)?.let { collection ->
      when (argument) {
        is Map<*, *> -> argument.toList()
        is Collection<*> -> argument.filterIsInstance<Pair<*, *>>()
        else -> null
      }?.run {
        val paramName = collection.name.ifBlank { parameter.parameterName }

        forEachIndexed { index, (key, value) ->
          val itemKey = key?.toString()
          val itemValue = value?.toString()

          if (!itemKey.isNullOrBlank() && !itemValue.isNullOrBlank()) {
            val itemKeyName = "$paramName[$index][${collection.keyName}]"
            val itemValueName = "$paramName[$index][${collection.valueName}]"

            requestValues.addRequestParameter(itemKeyName, itemKey)
            requestValues.addRequestParameter(itemValueName, itemValue)
          }
        }

        true
      }
    } ?: false
}
