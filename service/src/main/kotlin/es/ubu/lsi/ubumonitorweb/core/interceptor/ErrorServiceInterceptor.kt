package es.ubu.lsi.ubumonitorweb.core.interceptor

import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * Estructura JSON que devuelve el servicio de autenticación de Moodle al no
 * poder generar un token. El endpoint para obtener el token siempre responde en
 * formato JSON, por ello no hace falta mapear propiedades para `XmlMapper`.
 *
 * Referencia:
 * https://github.com/moodle/moodle/blob/main/public/login/token.php#L106
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
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
 * Estructura JSON que devuelve el servicio REST de Moodle ante errores. El
 * endpoint responde con el formato especificado mediante el parámetro
 * `moodlewsrestformat`, que puede recibir los valores `json` o `xml`, por lo
 * que las propiedades del `data class` se mapean a los campos del XML mediante
 * anotaciones; no hace falta mapearlas al JSON porque se utilizan los nombres
 * originales.
 *
 * Referencia:
 * https://github.com/moodle/moodle/blob/main/public/webservice/lib.php
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
data class RestError(
  @JacksonXmlProperty(isAttribute = true, localName = "class")
  val exception: String,

  @JacksonXmlProperty(localName = "ERRORCODE")
  val errorcode: String,

  @JacksonXmlProperty(localName = "MESSAGE")
  val message: String,
)

/**
 * Excepción para el manejo de errores de los servicios de Moodle. Los servicios
 * web de Moodle devuelven los errores con código de estado `200 OK`, por lo que
 * al interceptarlos se utiliza esta clase para relanzarlos y manejarlos
 * adecuadamente.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
class MoodleException : ResponseStatusException {
  companion object {
    /**
     * Mapeo de códigos de error de Moodle a códigos de estado HTTP.
     */
    private val STATUS_CODES = mapOf<String, HttpStatusCode>(
      "invalidlogin" to HttpStatus.UNAUTHORIZED,
      "invalidtoken" to HttpStatus.UNAUTHORIZED,
    )
  }

  /**
   * Sobrecarga del constructor para errores del servicio de autenticación.
   */
  constructor(e: AuthError) : super(
    STATUS_CODES.getValue(e.errorcode), e.error
  )

  /**
   * Sobrecarga del constructor para errores de los servicios REST.
   */
  constructor(e: RestError) : super(
    STATUS_CODES.getValue(e.errorcode), e.message
  )
}

/**
 * Interceptor que lee el cuerpo de la respuesta y si puede mapear el contenido
 * a error lanza una excepción.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ErrorServiceInterceptor(
  private val jsonMapper: ObjectMapper, private val xmlMapper: XmlMapper
) : ClientHttpRequestInterceptor {

  /**
   * Devuelve el mapper correspondiente de acuerdo con el header Content-Type.
   */
  private fun getMapper(contentType: MediaType?): ObjectMapper =
    if (contentType?.let {
        it.includes(MediaType.TEXT_XML) || it.includes(MediaType.APPLICATION_XML)
      } == true) xmlMapper else jsonMapper

  /**
   * Método interceptor.
   */
  override fun intercept(
    request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution
  ): ClientHttpResponse {
    val response = execution.execute(request, body)

    // Solo se habilita en respuestas con código 200
    if (response.statusCode.isSameCodeAs(HttpStatus.OK)) {
      val mapper = getMapper(response.headers.contentType)
      val tree = mapper.readTree(response.body)

      // Error del servicio de autenticación
      if (tree.has("errorcode") && tree.has("error")) {
        throw MoodleException(mapper.treeToValue(tree, AuthError::class.java))
      }

      // Error de los servicios REST
      if ((tree.has("errorcode") && tree.has("exception")) || (tree.has("class") && tree.has(
          "ERRORCODE"
        ))
      ) {
        throw MoodleException(mapper.treeToValue(tree, RestError::class.java))
      }
    }

    return response
  }
}
