package es.ubu.lsi.ubumonitorweb.core.rest

import es.ubu.lsi.ubumonitorweb.core.locale.Error
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriComponentsBuilder
import java.lang.reflect.Method
import java.net.URI
import java.net.URISyntaxException

/**
 * Procesador de la anotación que toma los metadatos definidos en las propiedades del servicio y los
 * inyecta en la solicitud saliente.
 *
 * @param request Solicitud entrante.
 * @param context Contexto de la aplicación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class MoodleServiceProcessor(
    private val request: HttpServletRequest,
    private val context: ApplicationContext,
) : HttpRequestValues.Processor {

  /**
   * Obtiene una anotación del método.
   *
   * @param T Clase de la anotación.
   * @return Anotación o `null` si no se encuentra.
   */
  private inline fun <reified T : Annotation> Method.annotation(): T? {
    return AnnotatedElementUtils.getMergedAnnotation(declaringClass, T::class.java)
  }

  override fun process(
      method: Method,
      parameters: Array<out MethodParameter>,
      arguments: Array<out Any?>,
      requestValues: HttpRequestValues.Builder,
  ) {
    method.annotation<MoodleService>()?.profile?.let { profile ->

      // Obtención de bean de propiedades
      context.getBean<MoodleProperties>().profiles.run {

        // Fusión de las propiedades indicadas en la anotación del servicio y fusión con las
        // propiedades por defecto, si están disponibles
        getOrDefault(
          profile,
          MoodleProperties.Profile(),
        ) merge getOrDefault(
          MoodleProperties.DEFAULT_PROFILE,
          MoodleProperties.Profile(),
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
          context.getBean<(Method) -> String>(value).invoke(method)
        }
        else {
          value
        }

        requestValues.addRequestParameter(key, processedValue)
      }
    }
  }
}
