package es.ubu.lsi.ubumonitorweb.core.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.MethodParameter
import java.lang.reflect.Method

/**
 * Propiedades de configuración de los servicios.
 *
 * En la versión anterior los servicios se configuraban con anotaciones, dando lugar a
 * configuraciones dispersas en el código fuente, algo con evidentes desventajas en caso de querer
 * realizar refactorizaciones, es por ello que en esta versión los metadatos de los servicios pasan
 * a residir exclusivamente en el fichero de configuración de la aplicación, bien `application.yaml`
 * o bien `application.properties`.
 *
 * De esta manera, los servicios ahora se anotan con:
 *
 * ```kotlin
 * @ServiceProfile("my-profile")
 * ```
 *
 * Y el procesador de la anotación construye la solicitud saliente de acuerdo con las propiedades
 * del servicio.
 *
 * @param default Perfil por defecto.
 * @param profiles Mapa de perfiles específicos.
 */
@ConfigurationProperties("service")
data class ServiceProperties(
    val default: Profile = Profile(),
    val profiles: Map<String, Profile> = HashMap(),
) {

  /**
   * Propiedades de configuración de un perfil.
   *
   * @param endpoint Endpoint de la solicitud saliente.
   * @param hostHeader Header que contiene el host de la solicitud saliente.
   * @param sendHeaders Conjunto de headers a reenviar desde la solicitud entrante hacia la
   * solicitud saliente.
   * @param headers Mapa de headers con sus valores para añadir a la solicitud saliente.
   * @param params Mapa de parámetros asociados a valores estáticos para añadir a la solicitud
   * saliente.
   * @param suppliers Mapa de parámetros asociados a proveedores de valores.
   */
  data class Profile(
      val endpoint: String = "",
      val hostHeader: String = "",
      val sendHeaders: Set<String> = emptySet(),
      val params: Map<String, String> = emptyMap(),
      val headers: Map<String, String> = emptyMap(),
      val cookies: Map<String, String> = emptyMap(),
      val suppliers: Map<String, Supplier> = emptyMap(),
  ) {

    data class Supplier(
        val location: Location,
        val bean: Class<out (Method, Map<MethodParameter, Any?>) -> Any?>,
    ) {
      enum class Location { PARAM, HEADER, COOKIE }
    }

    /**
     * Fusiona dos perfiles; en el caso de Strings tomando los valores del perfil de la derecha si
     * los valores son vacíos, en el caso de los conjuntos se produce una unión y, en el caso de los
     * mapas, se produce una fusión.
     *
     * La keyword `infix` permite utilizar `merge` como un operador binario, por ejemplo:
     *
     * ```kotlin
     * val profile3 = profile1 merge profile2
     * ```
     *
     * @param profile Perfil de la derecha de la operación.
     * @return Perfil resultante de la fusión.
     */
    infix fun merge(profile: Profile): Profile {
      return Profile(
        endpoint = endpoint.ifBlank { profile.endpoint },
        hostHeader = hostHeader.ifBlank { profile.hostHeader },
        sendHeaders = sendHeaders union profile.sendHeaders,
        headers = profile.headers + headers,
        params = profile.params + params,
        suppliers = profile.suppliers + suppliers,
      )
    }
  }
}
