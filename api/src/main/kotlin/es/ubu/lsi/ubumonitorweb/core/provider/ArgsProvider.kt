package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.ClientExtensions.httpParamName
import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestHeader

/**
 * La idea de este provider es entregar los argumentos de las
 * llamadas al endpoint `/lib/ajax/service.php`:
 *
 * Obtiene los parámetros del método que no son ni cookies
 * ni cabeceras y los coloca en la prop `args`.
 *
 * ```kotlin
 * @Client("ajax-client")
 * interface MyClient {
 *   @PostExchange
 *   fun myMethod(param1: Int, param2: String): Any
 * }
 * ```
 *
 * ```jsonc
 * [
 *   {
 *     "index": 0,
 *     "args": { "param1": ..., "param2": "..." }, // <- Resuelto por este provider
 *     "methodname": "..." // <- Resuelto por el FunctionProvider
 *   }
 * ]
 * ```
 */
@Component
class ArgsProvider : PropertyProvider.MethodAware<Map<String, Any>>() {
  private val MethodContext.filteredParams
    get() =
      params.filterKeys {
        it.hasParameterAnnotation(RequestHeader::class.java) || it.hasParameterAnnotation(CookieValue::class.java)
      }

  override fun invoke(methodContext: MethodContext): Map<String, Any> =
    methodContext.filteredParams.entries
      .mapNotNull { (param, arg) ->
        param.httpParamName?.let { name -> arg?.let { value -> name to value } }
      }.toMap()
}
