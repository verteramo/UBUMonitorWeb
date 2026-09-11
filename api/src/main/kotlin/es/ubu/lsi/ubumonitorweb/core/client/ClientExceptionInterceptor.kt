/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.xml.XmlMapper

/**
 * Interceptor que lee el cuerpo de la respuesta y, si se trata de algún error de Moodle, lanza una excepción.
 */
@Component
@RegisterReflection(
  classes = [
    ClientException::class,
    ClientException.AuthError::class,
    ClientException.RestError::class,
  ],
  memberCategories = [
    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
  ],
)
class ClientExceptionInterceptor(
  private val jsonMapper: ObjectMapper,
  private val xmlMapper: XmlMapper,
) : ClientHttpRequestInterceptor {
  /**
   * Determina si se trata de una respuesta del servicio.
   * 200 OK
   * Content-Type: image/png, image/jpeg, application/xml, application/json
   */
  private fun ClientHttpResponse.isServiceResponse(): Boolean =
    statusCode.isSameCodeAs(HttpStatus.OK) &&
      listOf(
        MediaType.IMAGE_PNG,
        MediaType.IMAGE_JPEG,
        MediaType.APPLICATION_XML,
        MediaType.APPLICATION_JSON,
      ).any { headers.contentType?.includes(it) == true }

  /**
   * Determina si se trata de una respuesta del formulario de login:
   * 200 OK/303 SEE OTHER
   * Content-Type: text/html
   * Set-Cookie: MoodleSession={...}
   */
  private fun ClientHttpResponse.isLoginFormResponse(): Boolean =
    statusCode in arrayOf(HttpStatus.OK, HttpStatus.SEE_OTHER) && headers.contentType?.includes(
      MediaType.TEXT_HTML,
    ) == true &&
      headers[HttpHeaders.SET_COOKIE]?.firstOrNull {
        it.startsWith("MoodleSession")
      } != null

  /**
   * TODO: Mejorar la detección de las respuestas del endpoint `/user/edit.php`, se deja temporalmente
   * de esta manera para avanzar, pero realmente un código de estado 200 con contenido HTML no identifica
   * claramente respuestas de servidores Moodle.
   *
   * Se podría considerar verificar la etqiueta HTML `meta`, que se ha observado que tiene un contenido similar a:
   * ```html
   * <meta name="keywords" content="moodle, ..." />
   * ```
   */
  private fun ClientHttpResponse.isEditFormResponse(): Boolean =
    statusCode.isSameCodeAs(HttpStatus.OK) && headers.contentType?.includes(
      MediaType.TEXT_HTML,
    ) == true

  /**
   * Mapper correspondiente al MediaType.
   */
  private val MediaType.mapper: ObjectMapper?
    get() =
      when {
        includes(MediaType.APPLICATION_XML) -> xmlMapper
        includes(MediaType.APPLICATION_JSON) -> jsonMapper
        else -> null
      }

  /**
   * Permite llamar a `tree.has` con múltiples props:
   * donde se haría: `tree.has("prop1") && tree.has("prop2") && ...`;
   * se simplifica en una única llamada: `tree.has("prop1", "prop2", ...)`
   */
  private fun JsonNode.has(vararg props: String): Boolean = props.all { has(it) }

  /**
   * Invocador del interceptor.
   */
  override fun intercept(
    request: HttpRequest,
    requestBody: ByteArray,
    requestExecution: ClientHttpRequestExecution,
  ): ClientHttpResponse =
    requestExecution.execute(request, requestBody).apply {
      if (isServiceResponse()) {
        // Si se trata de una respuesta del servicio, se intenta
        // inferir el error a partir del contenido del cuerpo de la respuesta
        headers.contentType?.mapper?.let { mapper ->
          mapper.readTree(body).let {
            if (it.has("errorcode", "error")) {
              throw ClientException(mapper.treeToValue(it, ClientException.AuthError::class.java))
            }

            if (it.has("errorcode", "message") || it.has("ERRORCODE", "MESSAGE")) {
              throw ClientException(mapper.treeToValue(it, ClientException.RestError::class.java))
            }
          }
        }
      } else if (!isLoginFormResponse() && !isEditFormResponse()) {
        // Si no, si no es una respuesta del formulario de login,
        // se determina que la respuesta no proviene de una aplicación Moodle
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, Message.ERROR_BAD_MOODLE())
      }
    }
}
