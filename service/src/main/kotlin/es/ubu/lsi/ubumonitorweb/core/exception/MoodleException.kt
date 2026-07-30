package es.ubu.lsi.ubumonitorweb.core.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * Excepción para el manejo de errores de los servicios de Moodle. Los servicios de Moodle devuelven
 * los errores con código de estado `200 OK`, por lo que al interceptarlos se utiliza esta clase
 * para relanzarlos y manejarlos adecuadamente.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
class MoodleException : ResponseStatusException {

  /**
   * Estructura JSON que devuelve el servicio de autenticación de Moodle al no poder generar un
   * token. El endpoint para obtener el token siempre responde en formato JSON, por ello no hace
   * falta mapear propiedades para `XmlMapper`.
   *
   * Referencia:
   * https://github.com/moodle/moodle/blob/main/public/login/token.php#L106
   */
  data class AuthError(
      val error: String,
      val errorcode: String,

    // Opcionales (si el administrador habilita depuración)
      val stacktrace: String?,
      val debuginfo: String?,
      val reproductionlink: String?,
  )

  /**
   * Estructura JSON que devuelven los servicios REST de Moodle ante errores. El endpoint responde
   * con el formato especificado mediante el parámetro `moodlewsrestformat`, que puede recibir los
   * valores `json` o `xml`, por lo que las propiedades del `data class` se mapean a los campos del
   * XML mediante anotaciones; no hace falta mapearlas al JSON porque se utilizan los nombres
   * originales.
   *
   * Referencia:
   * https://github.com/moodle/moodle/blob/main/public/webservice/lib.php
   */
  data class RestError(
      @JacksonXmlProperty(isAttribute = true, localName = "class") val exception: String,
      @JacksonXmlProperty(localName = "ERRORCODE") val errorcode: String,
      @JacksonXmlProperty(localName = "MESSAGE") val message: String,
  )

  companion object {
    /** Mapeo de códigos de error de Moodle a códigos de estado HTTP. */
    private val STATUS_CODES = mapOf(
      "invalidlogin" to HttpStatus.UNAUTHORIZED,
      "invalidtoken" to HttpStatus.UNAUTHORIZED,
    )
  }

  /**
   * Sobrecarga del constructor para errores del servicio de autenticación.
   *
   * @param e Error de autenticación.
   */
  constructor(e: AuthError) : super(
    STATUS_CODES.getValue(e.errorcode), e.error,
  )

  /**
   * Sobrecarga del constructor para errores de los servicios REST.
   *
   * @param e Error del servicio REST.
   */
  constructor(e: RestError) : super(
    STATUS_CODES.getValue(e.errorcode), e.message,
  )
}
