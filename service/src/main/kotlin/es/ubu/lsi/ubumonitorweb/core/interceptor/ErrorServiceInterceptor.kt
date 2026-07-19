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
 * Interceptor que lee el cuerpo de la respuesta y, si puede mapear el contenido a alguno de los dos
 * tipos de error de Moodle ([MoodleException.AuthError] o [MoodleException.RestError]), lanza una
 * excepción. Este interceptor es un [Component], sus mappers son inyectados por Spring.
 *
 * @param jsonMapper Mapper JSON.
 * @param xmlMapper Mapper XML.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ErrorServiceInterceptor(
    private val jsonMapper: ObjectMapper,
    private val xmlMapper: XmlMapper,
) : ClientHttpRequestInterceptor {

  /**
   * Función de extensión para evitar encadenar llamadas a `has` en verificaciones booleanas; donde
   * se haría: `tree.has("prop1") && tree.has("prop2") && ...`
   *
   * Se simplifica en una única llamada: `tree.has("prop1", "prop2", ...)`
   *
   * @param props Propiedades a verificar.
   * @return `true` si existen todas las propiedades, `false` en caso contrario.
   */
  private fun JsonNode.has(vararg props: String): Boolean {
    for (prop in props) {
      if (!has(prop)) {
        return false
      }
    }

    return true
  }

  /**
   * Función de extensión que obtiene el mapper correspondiente de acuerdo con el [MediaType] de la
   * respuesta.
   *
   * @param block Código de usuario con el mapper adecuado.
   */
  private fun MediaType?.useMapper(block: (ObjectMapper) -> Unit) {
    block(
      if (this?.let { includes(MediaType.TEXT_XML) || includes(MediaType.APPLICATION_XML) } == true) xmlMapper
      else jsonMapper,
    )
  }

  /**
   * Método interceptor.
   *
   * @param request Solicitud saliente.
   * @param body Cuerpo de la solicitud saliente.
   * @param execution Ejecutor para pasar la solicitud a los interceptores siguientes.
   * @return Respuesta para seguir siendo interceptada.
   */
  override fun intercept(
      request: HttpRequest,
      body: ByteArray,
      execution: ClientHttpRequestExecution,
  ): ClientHttpResponse {

    // Obtención de la respuesta, si quedan interceptores,
    // Spring Boot los llama sucesivamente hasta
    // obtener la respuesta final del servicio
    val response = execution.execute(request, body)

    // Solo se habilita en respuestas con código 200
    if (response.statusCode.isSameCodeAs(HttpStatus.OK)) {
      response.headers.contentType.useMapper { mapper ->

        val tree = mapper.readTree(response.body)

        // Error del servicio de autenticación
        if (tree.has("errorcode", "error")) {
          throw MoodleException(mapper.treeToValue(tree, MoodleException.AuthError::class.java))
        }

        // Error de los servicios REST
        if (tree.has("errorcode", "exception") || tree.has("class", "ERRORCODE")) {
          throw MoodleException(mapper.treeToValue(tree, MoodleException.RestError::class.java))
        }
      }
    }

    return response
  }
}
