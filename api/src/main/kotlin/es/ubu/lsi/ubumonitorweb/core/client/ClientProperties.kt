/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpStatus
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Propiedades de configuración de los clientes.
 *
 *
 * @param profiles Perfiles de clientes, más detalles en su definición (más abajo).
 * @param errorMappings Mapa de códigos de error de moodle a códigos de estado HTTP.
 */
@ConfigurationProperties("clients")
data class ClientProperties(
  val chunkSize: Int = 4096,
  val readTimeout: Duration = Duration.ofSeconds(15),
  val connectionTimeout: Duration = Duration.ofSeconds(3),
  val profiles: Map<String, Profile> = emptyMap(),
  val errorMappings: Map<String, HttpStatus> = emptyMap(),
) {
  /**
   * Propiedades de configuración de los perfiles.
   *
   * @param inherit Nombre de otro perfil del cual se heredan sus propiedades.
   * @param endpoint Path del servicio al que se realiza la solicitud.
   * @param multiple Indica si el body se envía como un array `[{...}, {...}, ...]`.
   * @param host Clase del bean que resuelve el host.
   * @param params Parámetros estáticos de la solicitud, no se resuelven dinámicamente, sino que van tal cual se definen,
   * cabe destacar que si el `Content-Type` es `application/x-www-form-urlencoded`, Spring los codifica y los adjunta en
   * el body, por ejemplo: `param1=value1&param2=value2&paramN=valueN`.
   * @param headers Igual que params, pero para adjuntar cabeceras.
   * @param cookies Igual que params y headers, pero para adjuntar cookies.
   * @param providers Similar a los anteriores, pero con parámetros que son resueltos por beans, se debe especificar la
   * ubicación donde se inyectará el valor (`param, header, cookie`) y el bean que lo resuelve.
   * @param forwardHeaders Permite reenviar cabecera desde la solicitud entrante hacia la solicitud saliente, por ejemplo
   * podría ser útil reenviar la cabecera `Accept-Language`, para que Moodle genere las respuestas en el lenguaje que
   * utiliza el usuario en el cliente.
   *
   * Ejemplo:
   * ```yaml
   * clients:
   *   profiles:
   *     profile1:
   *       endpoint: /login/index.php
   *       host: es.ubu.lsi.ubumonitorweb.MyHostProvider
   *       params:
   *         param1: value1
   *       headers:
   *         Content-Type: application/x-www-form-urlencoded
   *         Header1: value1
   *       cookies:
   *         Cookie1: value1
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
   * VERB ${host}/login/index.php // <-- Host resuelto por el bean, que lo puede buscar en una cabecera o donde sea.
   * Content-Type: application/x-www-form-urlencoded
   * Header1: value1
   * Set-Cookie: Cookie1=value1
   * Accept-Language: {...} // <-- El valor de la cabecera en la solicitud entrante.
   *
   * param1=value1&provided-value1={...} // <-- Valor resuelto por el proveedor, que podría ser un token guardado en memoria.
   * ```
   */
  data class Profile(
    val inherit: String = "",
    val endpoint: String = "",
    val multiple: Boolean = false,
    val host: Class<out PropertyProvider<*>>? = null,
    val params: Map<String, Any?> = emptyMap(),
    val headers: Map<String, String> = emptyMap(),
    val cookies: Map<String, String> = emptyMap(),
    val providers: Map<String, Provider> = emptyMap(),
    val forwardHeaders: Set<String> = emptySet(),
  ) {
    /**
     * Propiedades de configuración de los proveedores.
     *
     * @param location Ubicación final donde se inyectará el valor resuelto por el proveedor.
     * @param bean Clase del bean/component que resuelve el valor.
     */
    data class Provider(
      val location: Location,
      val bean: Class<out PropertyProvider<*>>,
    ) {
      /**
       * Ubicaciones posibles de los valores resueltos por los proveedores.
       */
      enum class Location { PARAM, HEADER, COOKIE }
    }

    /**
     * Fusiona dos perfiles; en el caso de Strings tomando los valores del perfil de la derecha si
     * los valores son vacíos, en el caso de los conjuntos se produce una unión y, en el caso de
     * los mapas, se produce una fusión. Por ejemplo:
     *
     * ```kotlin
     * val profile3 = profile1 merge profile2
     * ```
     */
    infix fun merge(parent: Profile) =
      Profile(
        inherit = inherit,
        endpoint = endpoint.ifBlank { parent.endpoint },
        multiple = multiple || parent.multiple,
        host = host ?: parent.host,
        params = parent.params + params,
        headers = parent.headers + headers,
        cookies = parent.cookies + cookies,
        providers = parent.providers + providers,
        forwardHeaders = parent.forwardHeaders union forwardHeaders,
      )
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
        error(Message.ERROR_PROFILE_INHERIT(name, parent))
      }
    }
  }

  /**
   * Permite acceder al mapa de perfiles con notación de arreglo/corchetes: `profiles["profile-name"]`
   */
  operator fun get(
    name: String,
    visited: Set<String> = emptySet(),
  ): Profile? =
    name.takeUnless { it.isBlank() || it in visited }?.let { profiles[it] }?.let {
      get(it.inherit, visited + name)?.let { parent -> it merge parent } ?: it
    }
}
