package es.ubu.lsi.ubumonitorweb.core.rest

import es.ubu.lsi.ubumonitorweb.core.base.ErrorResponse
import es.ubu.lsi.ubumonitorweb.core.base.SnakeCaseResolver
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriComponentsBuilder
import java.lang.reflect.Method
import java.net.URI
import java.net.URISyntaxException

/**
 * Interfaz funcional de un resolutor de parámetros.
 * Se puede encontrar un ejemplo en [SnakeCaseResolver].
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
fun interface ParamResolver : (Method) -> String

/**
 * Propiedades de configuración de los servicios de Moodle.
 *
 * En la versión anterior los servicios se configuraban con anotaciones, dando
 * lugar a configuraciones dispersas en el código fuente, algo con evidentes
 * desventajas en caso de querer realizar refactorizaciones, es por ello que en
 * esta versión, los metadatos de los servicios pasan a residir exclusivamente
 * en el fichero de configuración de la aplicación, bien `application.yaml` o
 * bien `application.properties`.
 *
 * De esta manera, los servicios ahora se anotan con
 *
 * ```kotlin
 * @MoodleService("nombre.del.bean")
 * ```
 *
 * y el procesador de la anotación construye la solicitud saliente de acuerdo
 * con las propiedades del servicio.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@ConfigurationProperties("moodle")
data class MoodleProps(
    val services: Map<String, ServiceProps> = HashMap(),
) {
  data class ServiceProps(
      val endpoint: String = "",
      val hostHeader: String = "",
      val headers: List<String> = emptyList(),
      val params: Map<String, String> = emptyMap(),
  )
}

/**
 * Anotación para servicios en la que se debe indicar el bean que contiene sus
 * propiedades, en este momento hay 2 bloques de configuración: `moodle.auth` y
 * `moodle.rest`.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS)
@HttpExchange(contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
annotation class MoodleService(val props: String)

/**
 * Procesador de la anotación que toma los metadatos definidos en las
 * propiedades del servicio y los inyecta en la solicitud saliente.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class MoodleServiceProcessor(
    private val request: HttpServletRequest,
    private val context: ApplicationContext,
) : HttpRequestValues.Processor {

  override fun process(
      method: Method,
      parameters: Array<out MethodParameter>,
      arguments: Array<out Any?>,
      builder: HttpRequestValues.Builder,
  ) {
    AnnotatedElementUtils.getMergedAnnotation(
      method.declaringClass,
      MoodleService::class.java,
    )?.props?.let {
      context.getBean<MoodleProps>().services[it]
    }?.let { props ->

      // Obtención del header
      val host = request.getHeader(props.hostHeader)

      // Si el header no existe o está vacío no se puede continuar
      if (host.isNullOrBlank()) {
        throw ErrorResponse.HTTP_MISSING_HEADER(props.hostHeader)
      }

      // Construcción del UriBuilderFactory
      try {
        builder.setUriBuilderFactory(
          DefaultUriBuilderFactory(
            UriComponentsBuilder.fromUri(URI(host + props.endpoint)),
          ),
        )
      } // Error de sintaxis en la URI
      catch (e: URISyntaxException) {
        throw ErrorResponse.NET_INVALID_URI(e, e.input)
      }

      // Propagación de headers desde la solicitud entrante hacia la solicitud
      // saliente
      for (name in props.headers) {
        request
            .getHeader(name)
            .takeIf { !it.isNullOrBlank() }
            ?.also { value -> builder.addHeader(name, value) }
      }


      // Construcción y paso de parámetros a la solicitud saliente
      for ((key, value) in props.params) {
        builder.addRequestParameter(
          key,
          if (!context.containsBean(value)) value
          else { // Si es un bean, se ejecuta para obtener el valor del parámetro
            context.getBean<ParamResolver>(value).invoke(method)
          },
        )
      }
    }
  }
}
