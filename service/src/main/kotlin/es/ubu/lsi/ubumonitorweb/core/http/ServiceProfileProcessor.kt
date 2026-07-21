package es.ubu.lsi.ubumonitorweb.core.http

import es.ubu.lsi.ubumonitorweb.core.locale.Error
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
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
   * Obtiene el perfil del servicio si la clase del método está anotada con [ServiceProfile].
   *
   * @return Perfil del servicio o `null` si la clase del método no está anotada.
   */
  private fun Method.getProfile(): String? {
    return AnnotatedElementUtils.getMergedAnnotation(
      declaringClass,
      ServiceProfile::class.java,
    )?.profile
  }

  /**
   * Obtiene los perfiles de los servicios desde la configuración de la aplicación.
   *
   * @return Mapa de perfiles.
   */
  private fun ApplicationContext.getProfiles(): Map<String, ServiceProperties.Profile> {
    return getBean<ServiceProperties>().profiles
  }

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
    method.getProfile()?.let { profile ->
      context.getProfiles().run {

        // Fusión de las propiedades indicadas en la anotación del servicio y fusión con las
        // propiedades por defecto, si están disponibles. En caso de no existir definiciones el mapa
        // contendrá un único perfil con valores vacíos por defecto.
        getOrDefault(
          profile,
          ServiceProperties.Profile(),
        ) merge getOrDefault(
          ServiceProperties.DEFAULT_PROFILE,
          ServiceProperties.Profile(),
        )
      }
    }?.let { profile ->

      // Obtención del header
      val host = request.getHeader(profile.hostHeader)

      // Si el header no existe o está vacío no se puede continuar
      if (host.isNullOrBlank()) {
        throw Error.HTTP_MISSING_HEADER(profile.hostHeader)
      }

      // Construcción del UriBuilderFactory
      try {
        requestValues.setUriBuilderFactory(
          DefaultUriBuilderFactory(
            UriComponentsBuilder.fromUri(URI(host + profile.endpoint)),
          ),
        )
      }

      // Error de sintaxis en la URI
      catch (e: URISyntaxException) {
        throw Error.NET_INVALID_URI(e, e.input)
      }

      // Propagación de headers desde la solicitud entrante hacia la solicitud saliente
      for (name in profile.sendHeaders) {
        request
            .getHeader(name)
            .takeIf { !it.isNullOrBlank() }
            ?.also { value -> requestValues.addHeader(name, value) }
      }

      // Paso de cabeceras
      for ((key, value) in profile.headers) {
        requestValues.addHeader(key, value)
      }

      // Construcción y paso de parámetros a la solicitud saliente
      for ((key, value) in profile.params) {
        val processedValue = if (context.containsBean(value)) {
          // Si es un bean, se crea un contexto con el mismo y se ejecuta el proveedor
          with(context.getBean<ServicePropertySupplier<*>>(value)) {
            ServicePropertySupplier.Context(method, parameters.zip(arguments).toMap()).get()
          }.toString()
        }
        else {
          // Si no es un bean, se asume como el valor plano final del parámetro
          value
        }

        // Finalmente, se añade el parámetro a la solicitud saliente
        requestValues.addRequestParameter(key, processedValue)
      }
    }
  }
}
