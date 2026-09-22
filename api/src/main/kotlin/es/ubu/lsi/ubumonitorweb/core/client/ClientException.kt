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
 * Excepción para el manejo de errores de los servicios de Moodle. Los servicios de Moodle devuelven
 * los errores con código de estado `200 OK`, por lo que al interceptarlos se utiliza esta clase
 * para relanzarlos y manejarlos adecuadamente.
 */
class ClientException(
  val error: ClientError,
  mappings: Map<String, HttpStatus>,
) : ErrorResponseException(mappings.getOrDefault(error.status, HttpStatus.BAD_REQUEST)) {
  init {
    setDetail(error.detail)
  }

  /**
   * DTO para mapear errores de Moodle.
   *
   * @param code Código de error de Moodle.
   * @param message Mensaje de error original de Moodle.
   * @param node
   */
  data class ClientError(
    @JsonAlias("errorcode", "ERRORCODE") val code: String?,
    @JsonAlias("error", "message", "MESSAGE") val message: String?,
    @JsonAlias("exception") val node: JsonNode? = null,
  ) {
    /**
     * Resolución del código de estado final.
     */
    val status: String?
      get() = node?.get("errorcode")?.asString() ?: code

    /**
     * Resolución del mensaje de error final.
     */
    val detail: String?
      get() = node?.get("message")?.asString() ?: message
  }
}
