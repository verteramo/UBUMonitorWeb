/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientExtensions.httpParamName
import es.ubu.lsi.ubumonitorweb.core.client.ClientProperties.Profile.Provider.Location
import es.ubu.lsi.ubumonitorweb.core.locale.Message
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

/**
 * Procesador de la anotación [Client] que toma los metadatos definidos en las propiedades
 * del cliente y los inyecta en la solicitud saliente.
 */
@Component
class ClientProcessor(
  private val request: HttpServletRequest,
  private val context: ApplicationContext,
) : HttpRequestValues.Processor {
  /**
   * Expresión regular para la identificación de cambios de minúscula a mayúscula.
   */
  private val regex = Regex("(?<=[a-z])(?=[A-Z])")

  private val AnnotatedElement.profile: String?
    get() = AnnotatedElementUtils.getMergedAnnotation(this, Client::class.java)?.profile

  /**
   * Nombre del perfil para el cliente.
   *
   * Se obtiene la anotación [Client] de la clase del método,
   * si es que está anotada, y se resuelve el nombre del perfil,
   * que puede ser explícito:
   * ```
   * @Client("my-profile")
   * interface MyClient {...}
   * ```
   *
   * O implícito convirtiendo el nombre del cliente de camel case a kebab case
   * ```
   * @Client
   * interface MyClient {...}
   * ```
   *
   * En cuyo caso el nombre del perfil será `my-client`.
   */
  private fun Method.getProfile(): String = profile ?: declaringClass.profile ?: declaringClass.simpleName.replace(regex, "-").lowercase()

  /**
   * Resuelve el Content-Type a partir de los parámetros y argumentos del método;
   * si algún parámetro está anotado como `Content-Type`, se obtiene su argumento (valor).
   *
   * ```kotlin
   * @Client
   * interface MyClient {
   *   @PostExchange
   *   fun myMethod(@RequestHeader("Content-Type") contentType: String): Any
   * }
   * ```
   */
  private val PropertyProvider.MethodAware.MethodContext.contentType: String?
    get() =
      params.firstNotNullOfOrNull { (parameter, argument) ->
        if (parameter.getParameterAnnotation(RequestHeader::class.java)?.let {
            it.name.equals("Content-Type", true) ||
              it.value.equals(
                "Content-Type",
                true,
              ) || parameter.parameterName.equals("Content-Type", true)
          } == true
        ) {
          argument?.toString()
        } else {
          null
        }
      }

  /**
   * Resuelve la cabecera `Content-Type` de la solicitud entrante, si se está reenviando.
   *
   * ```yaml
   * clients:
   *   my-client:
   *     forward-headers:
   *       - Content-Type
   * ```
   */
  private val ClientProperties.Profile.forwardedContentType: String?
    get() =
      forwardHeaders.firstOrNull { it.equals("Content-Type", ignoreCase = true) }?.let {
        request.getHeader("Content-Type")
      }

  /**
   * Resuelve el `Content-Type` desde las propiedades de configuración del cliente.
   *
   * ```
   * clients:
   *   my-client:
   *     headers:
   *       Content-Type: application/json
   * ```
   */
  private val ClientProperties.Profile.contentType: String?
    get() = headers.entries.firstOrNull { it.key.equals("Content-Type", true) }?.value

  private fun PropertyProvider<*>.resolve(methodContext: PropertyProvider.MethodAware.MethodContext): Any? =
    when (this) {
      is PropertyProvider.Static<*> -> invoke()
      is PropertyProvider.MethodAware<*> -> invoke(methodContext)
    }

  /**
   * Invocador del procesador.
   */
  override fun process(
    method: Method,
    parameters: Array<out MethodParameter>,
    arguments: Array<out Any?>,
    requestValues: HttpRequestValues.Builder,
  ) {
    /*
     * Si el profile no está definido en la configuración, la
     * aplicación no debe ni siquiera arrancar, se puede entender
     * como un error de compilación.
     */
    method.getProfile().let { context.getBean<ClientProperties>()[it] }?.let { profile ->
      // Contexto para los providers
      val methodContext =
        PropertyProvider.MethodAware.MethodContext(
          method = method,
          params = parameters.zip(arguments).toMap(),
        )

      /*
       * Obtención y establecimiento del host:
       * 1. Se obtiene el bean del provider.
       * 2. Se ejecuta pasándole el contexto del método.
       * 3. Se convierte su valor a string.
       * 4. Se le añade el endpoint.
       * 5. Se crea el UriBuilderFactory.
       * 6. Se le pasa a requestValues para que construya las URL con
       *    este factory, que contiene el host y el endpoint inyectados.
       */
      profile.host
        ?.let(context::getBean)
        ?.resolve(methodContext)
        ?.toString()
        ?.let { it + profile.endpoint }
        ?.let(::DefaultUriBuilderFactory)
        ?.let(requestValues::setUriBuilderFactory)

      /*
       * Paso de valores estáticos (headers y cookies) a la solicitud saliente,
       * los valores van tal cual, no requieren ningún procesamiento.
       */
      profile.headers.forEach(requestValues::addHeader)
      profile.cookies.forEach(requestValues::addCookie)

      /*
       * Los parámetros de momento se acumulan en un mapa mutable,
       * aún no se ha determinado el Content-Type y de acuerdo con
       * su valor,
       * - los parámetros podrían en la URL,
       * - application/x-www-form-urlencoded: en el body,
       * - application/json: también en el body pero serializados como JSON.
       */
      val params = profile.params.toMutableMap()

      /*
       * Paso de valores dinámicos (resueltos por providers) a la solicitud saliente,
       * los valores de headers y cookies se van inyectando durante el proceso, pero
       * los parámetros se siguen acumulando en el mapa mutable.
       *
       * Si en la signatura del método existe algún parámetro con el mismo nombre que uno que
       * esté definido en la configuración, prevalece el del método y nunca se ejecuta
       * el provider.
       */
      profile.providers.forEach { (name, provider) ->
        if (parameters.none { it.httpParamName == name }) {
          context.getBean(provider.bean).resolve(methodContext).also {
            when (provider.location) {
              Location.HEADER -> requestValues.addHeader(name, it.toString())
              Location.COOKIE -> requestValues.addCookie(name, it.toString())
              Location.PARAM -> params[name] = it
            }
          }
        }
      }

      /*
       * Se determina el Content-Type, ya que si es una solicitud POST con
       * Content-Type 'application/json' los parámetros deben ir serializados
       * como JSON en el cuerpo del mensaje.
       */
      val contentType = methodContext.contentType ?: profile.forwardedContentType ?: profile.contentType

      if (requestValues.httpMethod == HttpMethod.POST && contentType is String &&
        MediaType
          .parseMediaType(contentType)
          .includes(MediaType.APPLICATION_JSON)
      ) {
        requestValues.setBodyValue(if (profile.multiple) listOf(params) else params)
      } else {
        params.forEach { (name, value) -> requestValues.addRequestParameter(name, value.toString()) }
      }

      /*
       * Finalmente, se inyectan los headers reenviados, si están disponibles
       */
      profile.forwardHeaders.forEach { name ->
        request.getHeader(name)?.takeIf { it.isNotBlank() }?.let { value ->
          requestValues.addHeader(name, value)
        }
      }

      requestValues.addAttribute("Method", method)
    } ?: error(Message.ERROR_PROFILE_NOT_FOUND(method.getProfile()))
  }
}
