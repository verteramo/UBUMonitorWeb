/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProperties.Profile.Provider.Location
import es.ubu.lsi.ubumonitorweb.core.locale.Message
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import java.lang.reflect.Method

/**
 * Procesador de la anotación [ClientProfile] que toma los metadatos definidos en las propiedades
 * del cliente y los inyecta en la solicitud saliente.
 */
@Component
class ClientProfileProcessor(
  private val request: HttpServletRequest,
  private val context: ApplicationContext,
) : HttpRequestValues.Processor {
  /** Expresión regular para la identificación de cambios de minúscula a mayúscula. */
  private val regex = Regex("(?<=[a-z])(?=[A-Z])")

  /** Nombre del perfil para el cliente. */
  private val Method.profile: String
    get() =
      AnnotatedElementUtils
        .getMergedAnnotation(
          declaringClass,
          ClientProfile::class.java,
        )?.profile
        ?.takeIf {
          it.isNotBlank()
        } ?: declaringClass.simpleName.replace(regex, "-").lowercase()

  /** Invocador del procesador. */
  override fun process(
    method: Method,
    parameters: Array<out MethodParameter>,
    arguments: Array<out Any?>,
    requestValues: HttpRequestValues.Builder,
  ) {
    method.profile
      .let { context.getBean<ClientProperties>()[it] }
      ?.let { profile ->
        // Contexto para los providers
        val clientPropertyProviderContext =
          ClientPropertyProvider.Context(
            method = method,
            params = parameters.zip(arguments).toMap(),
          )

        // Obtención y establecimiento del host
        profile.host
          ?.let(context::getBean)
          ?.invoke(clientPropertyProviderContext)
          ?.toString()
          ?.let { it + profile.endpoint }
          ?.let(::DefaultUriBuilderFactory)
          ?.let(requestValues::setUriBuilderFactory)

        // Paso de valores estáticos (headers, cookies y parámetros) a la solicitud saliente
        profile.headers.forEach(requestValues::addHeader)
        profile.cookies.forEach(requestValues::addCookie)
        profile.params.forEach(requestValues::addRequestParameter)

        // Paso de valores dinámicos (resueltos por providers) a la solicitud saliente
        profile.providers.forEach { (name, provider) ->
          context.getBean(provider.bean).invoke(clientPropertyProviderContext).toString().also {
            when (provider.location) {
              Location.HEADER -> requestValues.addHeader(name, it)
              Location.COOKIE -> requestValues.addCookie(name, it)
              Location.PARAM -> requestValues.addRequestParameter(name, it)
            }
          }
        }

        // Reenvío de headers a la solicitud, si están disponibles
        profile.forwardHeaders.forEach { name ->
          request.getHeader(name)?.takeIf { it.isNotBlank() }?.let { value ->
            requestValues.addHeader(name, value)
          }
        }
      } ?: error(Message.ERROR_PROFILE_NOT_FOUND(method.profile))
  }
}
