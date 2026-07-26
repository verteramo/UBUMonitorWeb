package es.ubu.lsi.ubumonitorweb.core.service

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.core.service.ServiceProperties.Profile.Supplier.Location
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriComponentsBuilder
import java.lang.reflect.Method
import java.net.URI
import java.net.URISyntaxException

/**
 * Procesador de la anotación [ServiceProfile] que toma los metadatos definidos en las propiedades
 * del servicio y los inyecta en la solicitud saliente.
 *
 * @param request Solicitud entrante.
 * @param context Contexto de la aplicación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ServiceProfileProcessor(
    private val request: HttpServletRequest,
    private val context: ApplicationContext,
) : HttpRequestValues.Processor {

  /**
   * Perfil del servicio, si la clase del método está anotada con [ServiceProfile].
   *
   * @return Perfil del servicio o `null` si la clase del método no está anotada.
   */
  private val Method.profile: String?
    get() = AnnotatedElementUtils.getMergedAnnotation(
      declaringClass,
      ServiceProfile::class.java,
    )?.profile

  /**
   * Procesador de la solicitud.
   *
   * @param method Método del servicio [HttpExchange].
   * @param parameters Parámetros del método.
   * @param arguments Argumentos de la llamada al método.
   * @param requestValues Builder de la solicitud saliente.
   */
  override fun process(
      method: Method,
      parameters: Array<out MethodParameter>,
      arguments: Array<out Any?>,
      requestValues: HttpRequestValues.Builder,
  ) {
    method.profile?.let { name ->
      context.getBean<ServiceProperties>().run { profiles[name]?.merge(default) }
    }?.run {

      // Obtención y establecimiento del host
      request.getHeader(hostHeader).takeUnless { it.isNullOrBlank() }?.also { host ->
        requestValues.setUriBuilderFactory(
          try {
            DefaultUriBuilderFactory(UriComponentsBuilder.fromUri(URI(host + endpoint)))
          }
          catch (e: URISyntaxException) {
            throw Message.ERROR_NET_INVALID_URI(HttpStatus.BAD_REQUEST, e.input)
          },
        )
      } ?: throw Message.ERROR_HTTP_MISSING_HEADER(HttpStatus.BAD_REQUEST, hostHeader)

      // Propagación de headers desde la solicitud entrante hacia la solicitud saliente
      sendHeaders.forEach { name ->
        request.getHeader(name).takeUnless { it.isNullOrBlank() }?.let { value ->
          requestValues.addHeader(name, value)
        }
      }

      params.forEach(requestValues::addRequestParameter)
      headers.forEach(requestValues::addHeader)
      cookies.forEach(requestValues::addCookie)

      if (suppliers.isNotEmpty()) {
        val params = parameters.zip(arguments).toMap()

        // Paso de parámetros dinámicos a la solicitud saliente
        for ((key, supplier) in suppliers) {
          context.getBean(supplier.bean).invoke(method, params)?.toString()?.also { value ->
            when (supplier.location) {
              Location.PARAM -> requestValues.addRequestParameter(key, value)
              Location.HEADER -> requestValues.addHeader(key, value)
              Location.COOKIE -> requestValues.addCookie(key, value)
            }
          }
        }
      }
    }
  }
}
