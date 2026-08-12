package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile.Properties.Profile.Provider.Location
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import java.lang.reflect.Method

/**
 * Procesador de la anotación [ClientProfile] que toma los metadatos definidos en las propiedades
 * del cliente y los inyecta en la solicitud saliente.
 *
 * @param request Solicitud entrante.
 * @param context Contexto de la aplicación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ClientProfileProcessor(
  private val request: HttpServletRequest,
  private val context: ApplicationContext,
) : HttpRequestValues.Processor {
  /**
   * Perfil del cliente, si la interfaz está anotada con [ClientProfile].
   *
   * @return Perfil del cliente o `null` si la interfaz no está anotada.
   */
  private val Method.profile: String?
    get() =
      AnnotatedElementUtils
        .getMergedAnnotation(
          declaringClass,
          ClientProfile::class.java,
        )?.profile

  /**
   * Procesador de la solicitud.
   *
   * @param method Método del cliente [HttpExchange].
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
    method.profile
      ?.let { name ->
        /*
         * Resolución del perfil del cliente, en este momento se realiza el
         * merge con el perfil configurado en inherit, si inherit no es vacío
         */
        context.getBean<ClientProfile.Properties>().run {
          profiles[name]?.let { profile ->
            profile.inherit
              .takeIf { it.isNotBlank() }
              ?.let { profiles[it] }
              ?.let { profile merge it } ?: profile
          }
        }
      }?.let { profile ->
        // Construcción del contexto para los providers
        val serviceContext =
          ClientPropertyProvider.Context(
            method = method,
            params = parameters.zip(arguments).toMap(),
          )

        // Obtención y establecimiento del host
        profile.host
          ?.let(context::getBean)
          ?.invoke(serviceContext)
          ?.toString()
          ?.let { it + profile.endpoint }
          ?.let(::DefaultUriBuilderFactory)
          ?.let(requestValues::setUriBuilderFactory)

        // Paso de valores estáticos (headers, cookies y parámetros) a la solicitud saliente
        profile.headers.forEach(requestValues::addHeader)
        profile.cookies.forEach(requestValues::addCookie)
        profile.params.forEach(requestValues::addRequestParameter)

        // Paso de valores dinámicos (resueltos por providers) a la solicitud saliente
        for ((key, provider) in profile.providers) {
          context.getBean(provider.bean).invoke(serviceContext).toString().also { value ->
            when (provider.location) {
              Location.HEADER -> requestValues.addHeader(key, value)
              Location.COOKIE -> requestValues.addCookie(key, value)
              Location.PARAM -> requestValues.addRequestParameter(key, value)
            }
          }
        }

        // Reenvío de headers a la solicitud, si están disponibles
        profile.forwardHeaders.forEach { name ->
          request.getHeader(name)?.takeUnless { it.isBlank() }?.let { value ->
            requestValues.addHeader(name, value)
          }
        }
      }
  }
}
