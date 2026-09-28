/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.Duration

/**
 * Propiedades de configuración de la capa de clientes HTTP.
 *
 * @property factory Propiedades de configuración de la factoría.
 * @property profiles Perfiles de clientes, más detalles en su definición.
 * @property serviceErrorMappings Mapa de códigos de error de moodle a códigos de estado HTTP.
 * @property scrapingErrorMappings Mapa de códigos de estado HTTP del frontend de Moodle a códigos de estado HTTP y
 *                                 selector CSS del elemento HTML que contiene el mensaje de error.
 */
@ConfigurationProperties("clients")
data class ClientProperties(
  val factory: FactoryProperties = FactoryProperties(),
  val profiles: Map<String, Profile> = emptyMap(),
  val serviceErrorMappings: Map<String, HttpStatus> = emptyMap(),
  val scrapingErrorMappings: Map<HttpStatus, ScrappingError> = emptyMap(),
) {
  /**
   * Propiedades de configuración de los perfiles de clientes HTTP.
   *
   * @property inherit Nombre de otro perfil del cual se heredan sus propiedades.
   * @property method Método HTTP.
   * @property endpoint Path del servicio al que se realiza la solicitud.
   * @property multiple Indica si el body se envía como un array `[{...}, {...}, ...]`.
   * @property host Clase del bean que resuelve el host.
   * @property params Parámetros estáticos de la solicitud, no se resuelven dinámicamente, sino que van tal cual se definen.
   * @property headers Igual que params, pero para adjuntar cabeceras.
   * @property cookies Igual que params y headers, pero para adjuntar cookies.
   * @property providers Similar a los anteriores, pero con parámetros que son resueltos por beans, se debe especificar la
   *                     ubicación donde se inyectará el valor (`param, header, cookie`) y el bean que lo resuelve.
   * @property forwardHeaders Permite reenviar cabeceras desde la solicitud entrante hacia la solicitud saliente, por ejemplo
   *                          podría ser útil reenviar la cabecera `Accept-Language`, para que Moodle genere las respuestas
   *                          en el lenguaje que utiliza el usuario en el cliente.
   *
   * Ejemplo:
   * ```yaml
   * clients:
   *   profiles:
   *     profile1:
   *       method: GET
   *       endpoint: /login/index.php
   *       host: es.ubu.lsi.ubumonitorweb.MyHostProvider
   *       headers:
   *         Header1: value1
   *         Content-Type: application/x-www-form-urlencoded
   *       cookies:
   *         Cookie1: value1
   *       params:
   *         param1: value1
   *       providers:
   *         provided-value1:
   *           location: param
   *           bean: es.ubu.lsi.ubumonitorweb.MyValueProvider
   *       forward-headers:
   *         - Accept-Language
   * ```
   *
   * El [ClientProcessor] intercepta la llamada y compone una solicitud saliente como la siguiente:
   *
   * ```http
   * GET ${host}/login/index.php          // <-- Host resuelto por el provider
   * Content-Type: application/x-www-form-urlencoded
   * Set-Cookie: Cookie1=value1
   * Accept-Language: {...}               // <-- El valor de la cabecera en la solicitud entrante
   *
   * param1=value1&provided-value1={...}  // <-- Valor resuelto por el provider
   * ```
   */
  data class Profile(
    val inherit: String = "",
    val method: HttpMethod? = null,
    val endpoint: String = "",
    val multiple: Boolean? = null,
    val host: Class<out PropertyProvider<*>>? = null,
    val params: Map<String, Any?> = emptyMap(),
    val headers: Map<String, String> = emptyMap(),
    val cookies: Map<String, String> = emptyMap(),
    val providers: Map<String, Provider> = emptyMap(),
    val forwardHeaders: Set<String> = emptySet(),
  ) {
    /**
     * Propiedades de configuración de los providers.
     *
     * @property location Ubicación final donde se inyectará el valor resuelto.
     * @property bean Clase del bean/component que resuelve el valor.
     */
    data class Provider(
      val location: Location,
      val bean: Class<out PropertyProvider<*>>,
    ) {
      /**
       * Ubicaciones posibles de los valores resueltos por los providers.
       */
      enum class Location { PARAM, HEADER, COOKIE }
    }

    /**
     * Fusiona dos perfiles; en el caso de Strings vacíos tomando los valores del perfil de la derecha si,
     * en el caso de los conjuntos se produce una unión y, en el caso de los mapas, se produce una fusión.
     *
     * ```kotlin
     * val profile3 = profile1 merge profile2
     * ```
     *
     * @param parent Perfil de la derecha (o del cual se "hereda").
     * @return Nuevo perfil resultado de la fusión.
     */
    infix fun merge(parent: Profile) =
      Profile(
        inherit = inherit,
        method = method ?: parent.method ?: HttpMethod.GET,
        endpoint = endpoint.ifBlank { parent.endpoint },
        multiple = multiple ?: parent.multiple ?: false,
        host = host ?: parent.host,
        params = parent.params + params,
        headers = parent.headers + headers,
        cookies = parent.cookies + cookies,
        providers = parent.providers + providers,
        forwardHeaders = parent.forwardHeaders union forwardHeaders,
      )
  }

  /**
   * Configuración de la factoría de conexiones HTTP.
   *
   * @property chunkSize Tamaño en bytes por bloque en transferencias fragmentadas.
   * @property readTimeout Tiempo máximo de espera entre paquetes de datos recibidos.
   * @property connectionTimeout Tiempo máximo para establecer la conexión TCP con el servidor.
   */
  data class FactoryProperties(
    val chunkSize: Int = 4096,
    val readTimeout: Duration = Duration.ofSeconds(15),
    val connectionTimeout: Duration = Duration.ofSeconds(3),
  )

  /**
   * Patrón de error semántico detectado mediante análisis de contenido HTML.
   *
   * @property status Código HTTP asociado al error detectado.
   * @property detailSelector Selector CSS para extraer la descripción del fallo del DOM.
   */
  data class ScrappingError(
    val status: HttpStatus,
    val detailSelector: String,
  )

  /**
   * Resuelve un perfil fusionando recursivamente las propiedades de sus antecesores.
   *
   * @param name Identificador del perfil a buscar.
   * @param visited Nombres procesados en la traza para prevenir ciclos de recursión.
   * @return Perfil combinado con sus jerarquías, o `null` si no existe o genera una referencia circular.
   */
  fun resolveProfile(
    name: String,
    visited: Set<String> = emptySet(),
  ): Profile? =
    name.takeUnless { it.isBlank() || it in visited }?.let { profiles[it] }?.let {
      resolveProfile(it.inherit, visited + name)?.let { parent -> it merge parent } ?: it
    }

  init {
    /*
     * Verifica las herencias entre perfiles durante el arranque de la aplicación,
     * en caso de no poder resolver una herencia se produce una excepción,
     * se podría entender como un error de compilación.
     *
     * ```yaml
     * clients:
     *   profiles:
     *     profile1:
     *       inherit: non-existent-profile
     * ```
     */
    profiles.forEach { (name, profile) ->
      profile.inherit.takeIf { it.isNotBlank() && it !in profiles }?.let { parent ->
        error(Message.ERROR_PROFILE_INHERITANCE(name, parent))
      }
    }
  }
}
