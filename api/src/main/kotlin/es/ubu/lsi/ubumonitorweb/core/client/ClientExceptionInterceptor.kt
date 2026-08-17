package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.xml.XmlMapper

/**
 * Interceptor que lee el cuerpo de la respuesta y, si se trata de alguno de los dos
 * errores de Moodle, lanza una excepción.
 *
 * @author Marcelo Verteramo Pérsico
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
      if (statusCode.isSameCodeAs(HttpStatus.OK)) {
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
      }
    }
}
