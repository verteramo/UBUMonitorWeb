package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponseException
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * Excepción para el manejo de errores de los servicios de Moodle. Los servicios de Moodle devuelven
 * los errores con código de estado `200 OK`, por lo que al interceptarlos se utiliza esta clase
 * para relanzarlos y manejarlos adecuadamente.
 *
 * @author Marcelo Verteramo Pérsico
 */
class ClientException(
  status: HttpStatus,
) : ErrorResponseException(status) {
  /**
   * Estructura JSON que devuelve el servicio de autenticación de Moodle.
   * https://github.com/moodle/moodle/blob/main/public/login/token.php#L106
   */
  data class AuthError(
    val error: String,
    val errorcode: String,
  )

  /**
   * Estructura JSON/XML que devuelven los servicios REST de Moodle.
   * https://github.com/moodle/moodle/blob/main/public/webservice/lib.php
   */
  data class RestError(
    @JacksonXmlProperty(localName = "ERRORCODE") val errorcode: String,
    @JacksonXmlProperty(localName = "MESSAGE") val message: String,
  )

  companion object {
    /** Mapeo de códigos de error de Moodle a códigos de estado HTTP. */
    private val STATUS_CODES =
      mapOf(
        "invalidlogin" to HttpStatus.UNAUTHORIZED,
        "invalidtoken" to HttpStatus.UNAUTHORIZED,
        "nopermissions" to HttpStatus.FORBIDDEN,
      )
  }

  /** Sobrecarga del constructor para errores del servicio de autenticación. */
  constructor(e: AuthError) : this(STATUS_CODES.getValue(e.errorcode)) {
    setDetail(e.error)
  }

  /** Sobrecarga del constructor para errores de los servicios REST. */
  constructor(e: RestError) : this(STATUS_CODES.getValue(e.errorcode)) {
    setDetail(e.message)
  }
}
