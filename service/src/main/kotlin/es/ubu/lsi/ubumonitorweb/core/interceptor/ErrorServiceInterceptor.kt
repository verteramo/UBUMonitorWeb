package es.ubu.lsi.ubumonitorweb.core.interceptor

import es.ubu.lsi.ubumonitorweb.core.exception.MoodleException
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
 * Interceptor que lee el cuerpo de la respuesta y, si puede mapear el contenido
 * a error, lanza una excepción.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ErrorServiceInterceptor(
    private val jsonMapper: ObjectMapper, private val xmlMapper: XmlMapper,
) : ClientHttpRequestInterceptor {

  private fun JsonNode.has(vararg props: String): Boolean {
    for (prop in props) {
      if (!has(prop)) {
        return false
      }
    }

    return true
  }

  private fun MediaType?.withMapper(block: (ObjectMapper) -> Unit) =
      block(
        if (this?.let {
              includes(MediaType.TEXT_XML) || includes(MediaType.APPLICATION_XML)
            } == true) xmlMapper
        else jsonMapper,
      )


  /**
   * Método interceptor.
   */
  override fun intercept(
      request: HttpRequest,
      body: ByteArray,
      execution: ClientHttpRequestExecution,
  ): ClientHttpResponse {
    val response = execution.execute(request, body)

    // Solo se habilita en respuestas con código 200
    if (response.statusCode.isSameCodeAs(HttpStatus.OK)) {
      response.headers.contentType.withMapper { mapper ->

        val tree = mapper.readTree(response.body)

        // Error del servicio de autenticación
        if (tree.has("errorcode", "error")) {
          throw MoodleException(
            mapper.treeToValue(
              tree, MoodleException.AuthError::class.java,
            ),
          )
        }

        // Error de los servicios REST
        if (
            tree.has("errorcode", "exception") ||
            tree.has("class", "ERRORCODE")
        ) {
          throw MoodleException(
            mapper.treeToValue(
              tree, MoodleException.RestError::class.java,
            ),
          )
        }
      }
    }

    return response
  }
}
