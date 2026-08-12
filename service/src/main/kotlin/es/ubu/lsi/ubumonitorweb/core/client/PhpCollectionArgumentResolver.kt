package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver

/**
 * Resolutor de argumentos HTTP que procesa colecciones anotadas con [PhpCollection].
 *
 * Transforma listas de objetos en parámetros de consulta o de formulario
 * utilizando la sintaxis de arrays indexados nativa de PHP. De esta forma, los datos enviados
 * se estructuran como `param[0][key]=k&param[0][value]=v`, permitiendo que el backend
 * los analice y exponga automáticamente como arrays multidimensionales en su variable `$_REQUEST`.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class PhpCollectionArgumentResolver : HttpServiceArgumentResolver {
  /**
   * Resolutor.
   *
   * @param argument Valor del argumento.
   * @param parameter Reflexión del parámetro.
   * @param requestValues Builder de la solicitud saliente.
   * @return `true` si el parámetro fue resuelto,`false` en caso contrario.
   */
  override fun resolve(
    argument: Any?,
    parameter: MethodParameter,
    requestValues: HttpRequestValues.Builder,
  ): Boolean {
    // Solo se procesa el argumento si tiene la anotación
    parameter.getParameterAnnotation(PhpCollection::class.java)?.let { collection ->
      val param = collection.name.ifBlank { parameter.parameterName }
      val keyName = collection.keyName
      val valueName = collection.valueName

      if (argument is Collection<*>) {
        argument.filterIsInstance<Pair<*, *>>().forEachIndexed { index, pair ->
          val key = "$param[$index][$keyName]"
          val value = "$param[$index][$valueName]"

          requestValues.addRequestParameter(key, pair.first.toString())
          requestValues.addRequestParameter(value, pair.second.toString())
        }
      }

      return true
    }

    return false
  }
}
