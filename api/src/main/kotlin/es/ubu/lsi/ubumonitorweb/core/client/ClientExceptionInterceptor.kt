/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
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
 * Interceptor que lee el cuerpo de la respuesta y, si se trata de alguno de los dos
 * errores de Moodle, lanza una excepción.
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
  /** Determina si se trata de una respuesta del servicio aceptable. */
  private fun ClientHttpResponse.isAcceptable(): Boolean =
    statusCode.isSameCodeAs(HttpStatus.OK) &&
      listOf(
        MediaType.IMAGE_PNG,
        MediaType.IMAGE_JPEG,
        MediaType.APPLICATION_XML,
        MediaType.APPLICATION_JSON,
      ).any { headers.contentType?.includes(it) == true }

  /** Mapper correspondiente al MediaType. */
  private val MediaType.mapper: ObjectMapper?
    get() =
      when {
        includes(MediaType.APPLICATION_XML) -> xmlMapper
        includes(MediaType.APPLICATION_JSON) -> jsonMapper
        else -> null
      }

  /**
   * Donde se haría: `tree.has("prop1") && tree.has("prop2") && ...`;
   * Se simplifica en una única llamada: `tree.has("prop1", "prop2", ...)`
   */
  private fun JsonNode.has(vararg props: String): Boolean = props.all { has(it) }

  /** Invocador del interceptor. */
  override fun intercept(
    request: HttpRequest,
    requestBody: ByteArray,
    requestExecution: ClientHttpRequestExecution,
  ): ClientHttpResponse =
    requestExecution.execute(request, requestBody).apply {
      if (isAcceptable()) {
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
      } else {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, Message.ERROR_BAD_MOODLE())
      }
    }
}
