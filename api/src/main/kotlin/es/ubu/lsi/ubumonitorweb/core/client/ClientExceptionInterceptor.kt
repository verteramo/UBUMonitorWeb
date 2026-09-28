/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.select
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.treeToValue

/**
 * Interceptor que lee el cuerpo de la respuesta y, si se trata de algún error de Moodle, lanza una excepción.
 *
 * @property properties Propiedades de configuración de la capa de clientes HTTP.
 * @property xmlMapper Mapper XML.
 * @property jsonMapper Mapper JSON.
 */
@Component
@RegisterReflection(
  /*
   * Registro de las clases que se pueden llegar a instanciar mediante reflexión,
   * para que GraalVM guarde el camino estático a las mismas.
   */
  classes = [
    ClientException::class,
    ClientException.ClientError::class,
  ],
  memberCategories = [
    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
  ],
)
class ClientExceptionInterceptor(
  private val properties: ClientProperties,
  private val xmlMapper: XmlMapper,
  private val jsonMapper: ObjectMapper,
) : ClientHttpRequestInterceptor {
  /**
   * Determina si se trata de una respuesta del frontend que puede llegar a tener un error incrustado.
   */
  private val ClientHttpResponse.isScrapingResponse: Boolean
    get() = statusCode.is4xxClientError && headers.contentType?.includes(MediaType.TEXT_HTML) == true

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
   * Intercepta y procesa la solicitud HTTP saliente.
   *
   * @param request Petición HTTP en curso.
   * @param requestBody Contenido en bytes del cuerpo enviado.
   * @param requestExecution Cadena de ejecución para delegar la llamada.
   * @return Respuesta HTTP devuelta por el servidor.
   */
  override fun intercept(
    request: HttpRequest,
    requestBody: ByteArray,
    requestExecution: ClientHttpRequestExecution,
  ): ClientHttpResponse =
    /*
     * Este interceptor interviene cuando se recibe la respuesta.
     */
    requestExecution.execute(request, requestBody).apply {
      if (isScrapingResponse) {
        /*
         * Errores del frontend de Moodle.
         * Si hay algún mapping definido para el código de estado, se lanza la excepción
         */
        val status = HttpStatus.valueOf(statusCode.value())
        properties.scrapingErrorMappings[status]?.let { mapping ->
          val detail = body.select(mapping.detailSelector)?.text()

          throw ClientException(mapping.status, detail)
        }
      }

      headers.contentType?.mapper?.let { mapper ->
        /*
         * Errores de los webservices de Moodle.
         * Si el cuerpo del mensaje encaja con el ClientError, se lanza la excepción.
         */
        mapper
          .readTree(body)
          .let { if (it.isArray) it.firstOrNull() else it }
          ?.takeIf { it.isObject }
          ?.let { mapper.treeToValue<ClientException.ClientError>(it) }
          ?.takeIf { it.status != null }
          ?.let {
            val status = properties.serviceErrorMappings.getOrDefault(it.status, HttpStatus.BAD_REQUEST)
            throw ClientException(status, it.detail)
          }
      }
    }
}
