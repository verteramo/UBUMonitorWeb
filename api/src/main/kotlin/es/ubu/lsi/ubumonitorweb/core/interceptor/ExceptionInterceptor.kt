/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.interceptor

import es.ubu.lsi.ubumonitorweb.core.client.ClientException
import es.ubu.lsi.ubumonitorweb.core.client.ClientProperties
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.treeToValue

/**
 * Interceptor que lee el cuerpo de la respuesta y,
 * si se trata de algún error de Moodle, lanza una excepción.
 */
@Component
@RegisterReflection(
  /*
   * Registro de las clases que se pueden llegar a
   * instanciar mediante reflexión, para que GraalVM
   * guarde el camino estático a las mismas.
   */
  classes = [
    ClientException::class,
    ClientException.ClientError::class,
  ],
  memberCategories = [
    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
  ],
)
class ExceptionInterceptor(
  private val properties: ClientProperties,
  private val jsonMapper: ObjectMapper,
  private val xmlMapper: XmlMapper,
) : ClientHttpRequestInterceptor {
  /**
   * Obtiene el mapper correspondiente al MediaType.
   */
  private val MediaType.mapper: ObjectMapper?
    get() =
      when {
        includes(MediaType.APPLICATION_XML) -> xmlMapper
        includes(MediaType.APPLICATION_JSON) -> jsonMapper
        else -> null
      }

  /**
   * Invocador del interceptor.
   *
   * 1. Se mapea el body de InputStream a JsonNode.
   * 2. Si es un array, se obtiene el primer elemento (los errores de Ajax vienen en un array).
   * 3.
   */
  override fun intercept(
    request: HttpRequest,
    requestBody: ByteArray,
    requestExecution: ClientHttpRequestExecution,
  ): ClientHttpResponse =
    requestExecution.execute(request, requestBody).apply {
      request.takeUnless { it.attributes["skipExceptionInterceptor"] == true }?.let {
        headers.contentType?.mapper?.let { mapper ->
          mapper
            .readTree(body)
            .let { if (it.isArray) it.firstOrNull() else it }
            ?.takeIf { it.isObject }
            ?.let { mapper.treeToValue<ClientException.ClientError>(it) }
            ?.takeIf { it.status != null }
            ?.let { throw ClientException(it, properties.errorMappings) }
        }
      }
    }
}
