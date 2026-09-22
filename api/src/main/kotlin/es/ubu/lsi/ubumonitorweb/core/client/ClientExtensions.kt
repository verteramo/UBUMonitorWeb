/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import kotlin.text.ifEmpty

/**
 * Extensiones de utilidad para las clases del paquete client.
 */
object ClientExtensions {
  /**
   * Resuelve el nombre que tendrá el parámetro una vez inyectado en la solicitud HTTP.
   * Por ejemplo, si el parámetro estuviera anotado como:
   *
   * ```kotlin
   * @RequestParam("httpParamName") paramName
   * ```
   */
  internal val MethodParameter.httpParamName: String?
    get() =
      getParameterAnnotation(RequestHeader::class.java)?.let {
        it.name.ifEmpty { it.value }.ifEmpty { parameterName }
      } ?: getParameterAnnotation(RequestParam::class.java)?.let {
        it.name.ifEmpty { it.value }.ifEmpty { parameterName }
      } ?: getParameterAnnotation(CookieValue::class.java)?.let {
        it.name.ifEmpty { it.value }.ifEmpty { parameterName }
      } ?: parameterName
}
