/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import com.fasterxml.jackson.annotation.JsonAlias
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponseException
import tools.jackson.databind.JsonNode

/**
 * Excepción para el manejo de errores de los servicios de Moodle.
 *
 * Los servicios de Moodle devuelven los errores con código de estado `200 OK`,
 * por lo que al interceptarlos se utiliza esta clase para relanzarlos y manejarlos
 * adecuadamente.
 *
 * Error del endpoint /login/token.php:
 * ```
 * {
 *   "error": "A required parameter (username) was missing",
 *   "errorcode": "missingparam",
 *   "stacktrace": null,
 *   "debuginfo": null,
 *   "reproductionlink": null
 * }
 * ```
 *
 * Error del endpoint /lib/ajax/service.php:
 * ```
 * {
 *   "error": "Coding error detected, it must be fixed by a programmer: Invalid json in request: Syntax error",
 *   "errorcode": "codingerror",
 *   "stacktrace": null,
 *   "debuginfo": null,
 *   "reproductionlink": null
 * }
 * ```
 *
 * Error anidado del endpoint /lib/ajax/service.php:
 * ```
 * [
 *   {
 *     "error": true,
 *     "exception": {
 *       "message": "Invalid parameter value detected",
 *       "errorcode": "invalidparameter",
 *       "link": "https://school.moodledemo.net/",
 *       "moreinfourl": "http://docs.moodle.org/502/en/error/debug/invalidparameter"
 *     }
 *   }
 * ]
 * ```
 *
 * Error JSON del endpoint /webservice/rest/server.php:
 * ```
 * {
 *   "exception": "core\\exception\\moodle_exception",
 *   "errorcode": "invalidtoken",
 *   "message": "Invalid token - token not found"
 * }
 * ```
 *
 * Error XML del endpoint /webservice/rest/server.php:
 * ```
 * <?xml version="1.0" encoding="UTF-8"?>
 * <EXCEPTION class="core\exception\moodle_exception">
 *     <ERRORCODE>invalidtoken</ERRORCODE>
 *     <MESSAGE>Invalid token - token not found</MESSAGE>
 * </EXCEPTION>
 * ```
 *
 * @param status Código de estado HTTP.
 * @param detail Mensaje detallado del error.
 */
class ClientException(
  status: HttpStatus,
  detail: String?,
) : ErrorResponseException(status) {
  init {
    detail?.let { setDetail(it) }
  }

  /**
   * DTO preparado para mapear errores de Moodle.
   *
   * @property code Código de error de Moodle.
   * @property message Mensaje de error original de Moodle.
   * @property node Posible nodo que incluyen los errores del servicio Ajax.
   */
  data class ClientError(
    @JsonAlias("errorcode", "ERRORCODE") val code: String?,
    @JsonAlias("error", "message", "MESSAGE") val message: String?,
    @JsonAlias("exception") val node: JsonNode? = null,
  ) {
    /**
     * Evalúa y extrae de forma segura el nodo interno de la excepción.
     */
    private val nodeAsObject: JsonNode?
      get() = node?.takeIf { it.isObject }

    /**
     * Código de estado final.
     */
    val status: String?
      get() = nodeAsObject?.get("errorcode")?.asString() ?: code

    /**
     * Mensaje de error final.
     */
    val detail: String?
      get() = nodeAsObject?.get("message")?.asString() ?: message?.takeIf { it != "true" }

    /**
     * Indica si el DTO ha mapeado exitosamente un error legítimo de Moodle.
     */
    val isValid: Boolean
      get() = !status.isNullOrBlank() && !detail.isNullOrBlank()
  }
}
