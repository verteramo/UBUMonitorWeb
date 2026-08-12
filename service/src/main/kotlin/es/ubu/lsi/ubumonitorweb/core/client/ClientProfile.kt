package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Anotación para clientes en la que se indica el perfil que contiene sus propiedades y metadatos
 * en el fichero de configuración de la aplicación. Por ejemplo:
 *
 * ```yaml
 * clients:
 *   profiles:
 *     my-profile:
 *       host: # Bean que resuelve el host
 *       headers:
 *         Accept-Language: en
 *         Content-Type: application/x-www-form-urlencoded
 * ```
 *
 * ```kotlin
 * @ClientProfile("my-profile")
 * ```
 *
 * @param profile Nombre del perfil de la configuración.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS)
annotation class ClientProfile(
  val profile: String,
) {
  /**
   * Propiedades de configuración.
   *
   * @param profiles Mapa de perfiles.
   */
  @ConfigurationProperties("clients")
  data class Properties(
    val profiles: Map<String, Profile> = emptyMap(),
  ) {
    /**
     * Propiedades de configuración de perfiles.
     *
     * @param inherit Perfil del cual se heredan sus propiedades (opcional).
     * @param endpoint Endpoint de la solicitud saliente.
     * @param host Bean del provider que resuelve el host.
     * @param params Mapa de parámetros asociados a valores estáticos.
     * @param headers Mapa de headers asociados a valores estáticos.
     * @param cookies Mapa de cookies asociadas a valores estáticos.
     * @param providers Mapa de parámetros asociados a proveedores de valores.
     * @param forwardHeaders Conjunto de headers a reenviar desde la solicitud entrante hacia la
     * solicitud saliente.
     */
    data class Profile(
      val inherit: String = "",
      val endpoint: String = "",
      val host: Class<out ClientPropertyProvider<*>>? = null,
      val params: Map<String, String> = emptyMap(),
      val headers: Map<String, String> = emptyMap(),
      val cookies: Map<String, String> = emptyMap(),
      val providers: Map<String, Provider> = emptyMap(),
      val forwardHeaders: Set<String> = emptySet(),
    ) {
      /**
       * Propiedades de configuración de proveedores.
       *
       * @param location Ubicación de destino del valor resuelto por el proveedor.
       * @param bean Bean del proveedor.
       */
      data class Provider(
        val location: Location,
        val bean: Class<out ClientPropertyProvider<*>>,
      ) {
        /** Ubicaciones posibles de los valores resueltos por los proveedores. */
        enum class Location { PARAM, HEADER, COOKIE }
      }

      /**
       * Fusiona dos perfiles; en el caso de Strings tomando los valores del perfil de la derecha si
       * los valores son vacíos, en el caso de los conjuntos se produce una unión y, en el caso de
       * los mapas, se produce una fusión.
       *
       * La keyword `infix` permite utilizar `merge` como un operador binario, por ejemplo:
       *
       * ```kotlin
       * val profile3 = profile1 merge profile2
       * ```
       *
       * @param parent Perfil de la derecha de la operación.
       * @return Perfil resultante de la fusión.
       */
      infix fun merge(parent: Profile) =
        Profile(
          inherit = inherit,
          endpoint = endpoint.ifBlank { parent.endpoint },
          host = host ?: parent.host,
          params = parent.params + params,
          headers = parent.headers + headers,
          cookies = parent.cookies + cookies,
          providers = parent.providers + providers,
          forwardHeaders = parent.forwardHeaders union forwardHeaders,
        )
    }
  }
}
