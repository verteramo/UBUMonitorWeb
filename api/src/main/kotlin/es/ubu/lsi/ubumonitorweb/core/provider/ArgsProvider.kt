/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.ClientExtensions.httpParamName
import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestHeader
import kotlin.reflect.KClass

/**
 * Provider que resuelve los argumentos de las llamadas al endpoint `/lib/ajax/service.php`;
 * Obtiene los parámetros del método que no son ni cabeceras ni cookies:
 *
 * ```kotlin
 * @Client("ajax-client")
 * interface MyClient {
 *   @PostExchange
 *   fun myMethod(param1: Int, param2: String): Any
 * }
 * ```
 *
 * Y construye el mapa para la propiedad `args` del objeto de salida que requiere el servicio Ajax:
 *
 * ```jsonc
 * [
 *   {
 *     "index": 0,
 *     "args": { "param1": 7, "param2": "..." }, // <- Resuelto por este provider
 *     "methodname": "..."                       // <- Resuelto por el FunctionProvider
 *   }
 * ]
 * ```
 *
 * Quien ensambla el objeto final es el ClientProcessor.
 *
 * @see es.ubu.lsi.ubumonitorweb.core.client.ClientProcessor
 */
@Component
class ArgsProvider : PropertyProvider.MethodAware<Map<String, Any>>() {
  /**
   * Comprueba si el parámetro posee alguna de las anotaciones especificadas.
   *
   * @param annotations Clases de las anotaciones a comprobar.
   * @return `true` si contiene al menos una de las anotaciones, `false` en caso contrario.
   */
  private fun MethodParameter.isAnnotatedWith(vararg annotations: KClass<out Annotation>): Boolean =
    annotations.any { hasParameterAnnotation(it.java) }

  /**
   * Parámetros del método que no son ni cabeceras ni cookies.
   */
  private val MethodContext.filteredParams
    get() = params.filterKeys { !it.isAnnotatedWith(RequestHeader::class, CookieValue::class) }

  /**
   * Resuelve el mapa de argumentos para la petición descartando cabeceras y cookies.
   *
   * @param methodContext Contexto del método HTTP.
   * @return Mapa que asocia el nombre HTTP de cada parámetro con su valor.
   */
  override fun invoke(methodContext: MethodContext): Map<String, Any> =
    methodContext.filteredParams.entries
      .mapNotNull { it.key.httpParamName?.let { name -> it.value?.let { value -> name to value } } }
      .toMap()
}
