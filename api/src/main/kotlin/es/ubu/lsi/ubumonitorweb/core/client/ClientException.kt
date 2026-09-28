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
   * Errores de los webservices en XML y JSON, y del endpoint Ajax.
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
     * Código de estado final.
     */
    val status: String?
      get() = node?.get("errorcode")?.asString() ?: code

    /**
     * Mensaje de error final.
     */
    val detail: String?
      get() = node?.get("message")?.asString() ?: message
  }
}
