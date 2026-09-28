/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

/**
 * Utilidades de resolución para metadatos de parámetros en clientes HTTP.
 * Si recopilan aquí para utilizarse en varios contextos.
 */
object ClientExtensions {
  /**
   * Resuelve el nombre del parámetro para su inyección en la petición HTTP.
   *
   * Intenta inferir el nombre desde las anotaciones [RequestHeader], [RequestParam] y [CookieValue],
   * se utiliza como fallback el nombre del propio parámetro [MethodParameter.getParameterName].
   *
   * ```kotlin
   * @RequestParam("httpParamName") paramName: String // -> "httpParamName"
   * @RequestParam paramName: String                  // -> "paramName"
   * ```
   */
  val MethodParameter.httpParamName: String?
    get() =
      (
        getParameterAnnotation(RequestHeader::class.java)?.run { name.ifBlank { value } }
          ?: getParameterAnnotation(RequestParam::class.java)?.run { name.ifBlank { value } }
          ?: getParameterAnnotation(CookieValue::class.java)?.run { name.ifBlank { value } }
      )?.takeIf { it.isNotBlank() } ?: parameterName

  /**
   * Indica si el parámetro del método es una cabecera `Content-Type`.
   */
  val MethodParameter.isContentType: Boolean
    get() =
      getParameterAnnotation(RequestHeader::class.java)
        ?.let { it.value.ifBlank { it.name }.ifBlank { parameterName } }
        .equals(HttpHeaders.CONTENT_TYPE, true)
}
