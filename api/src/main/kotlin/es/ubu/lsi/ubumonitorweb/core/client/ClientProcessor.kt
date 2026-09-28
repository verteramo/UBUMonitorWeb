/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientExtensions.httpParamName
import es.ubu.lsi.ubumonitorweb.core.client.ClientExtensions.isContentType
import es.ubu.lsi.ubumonitorweb.core.client.ClientProperties.Profile.Provider.Location
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.DefaultUriBuilderFactory
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

/**
 * Procesador de la anotación [Client] que toma los metadatos definidos en el perfil
 * del cliente y los inyecta en la solicitud saliente.
 *
 * @property request Solicitud entrante.
 * @property context Contexto de aplicación (resolución de beans).
 */
@Component
class ClientProcessor(
  private val request: HttpServletRequest,
  private val context: ApplicationContext,
) : HttpRequestValues.Processor {
  /**
   * Nombre del perfil con el que está anotado el elemento, si lo está.
   */
  private val AnnotatedElement.profile: String?
    get() = AnnotatedElementUtils.getMergedAnnotation(this, Client::class.java)?.profile?.takeIf { it.isNotBlank() }

  /**
   * Resuelve el nombre del perfil del cliente, se utiliza como fallback el de su interfaz, si tiene.
   *
   * @return Nombre del perfil.
   */
  private fun Method.resolveProfile(): String? = profile ?: declaringClass.profile

  /**
   * Resuelve el `Content-Type` a partir de los parámetros y argumentos del método.
   */
  private val Map<MethodParameter, Any?>.contentType: String?
    get() = firstNotNullOfOrNull { (param, arg) -> if (param.isContentType) arg?.toString() else null }

  /**
   * Resuelve el `Content-Type` de la solicitud entrante, si se está reenviando.
   */
  private val ClientProperties.Profile.forwardedContentType: String?
    get() = if (forwardHeaders.contains(HttpHeaders.CONTENT_TYPE)) request.getHeader(HttpHeaders.CONTENT_TYPE) else null

  /**
   * Determina si una cadena es parseable como `application/json`.
   */
  private val String.isJsonMetaType: Boolean
    get() = MediaType.parseMediaType(this).includes(MediaType.APPLICATION_JSON)

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
     * Si el profile no está definido en la configuración, la aplicación no
     * debe ni siquiera arrancar, se puede entender como un error de compilación.
     */
    method
      .resolveProfile()
      ?.let { context.getBean<ClientProperties>().resolveProfile(it) }
      ?.let { profile ->
        // Contexto para los providers
        val methodContext =
          PropertyProvider.MethodAware.MethodContext(
            method = method,
            params = parameters.zip(arguments).toMap(),
          )

        // Establecimiento del método HTTP.
        requestValues.setHttpMethod(profile.method ?: HttpMethod.GET)

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
         * se le deben añadir los parámetros dinámicos resueltos por
         * providers y, finalmente, de acuerdo con el Content-Type:
         * - los parámetros podrían en la URL,
         * - application/x-www-form-urlencoded: en el body,
         * - application/json: también en el body pero serializados como JSON.
         */
        val params = profile.params.toMutableMap()
        val contentType =
          methodContext.params.contentType ?: profile.forwardedContentType ?: profile.headers[HttpHeaders.CONTENT_TYPE]

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
         * Si es una solicitud POST con Content-Type 'application/json',
         * los parámetros deben ir serializados como JSON en el cuerpo del mensaje.
         */
        if (requestValues.httpMethod == HttpMethod.POST && contentType?.isJsonMetaType == true) {
          requestValues.setBodyValue(if (profile.multiple == true) listOf(params) else params)
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

        /*
         * Los atributos no viajan por la red al destino, no son parte de la solicitud HTTP,
         * pero son útiles para los interceptores.
         */
        requestValues.addAttribute("Method", method)
      }
  }
}
