package es.ubu.lsi.ubumonitorweb.core.http

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Propiedades de configuración de los servicios.
 *
 * En la versión anterior los servicios se configuraban con anotaciones, dando lugar a
 * configuraciones dispersas en el código fuente, algo con evidentes desventajas en caso de querer
 * realizar refactorizaciones, es por ello que en esta versión los metadatos de los servicios pasan
 * a residir exclusivamente en el fichero de configuración de la aplicación, bien `application.yaml`
 * o bien `application.properties`.
 *
 * De esta manera, los servicios ahora se anotan con
 *
 * ```kotlin
 * @ServiceProfile("profile")
 * ```
 *
 * y el procesador de la anotación construye la solicitud saliente de acuerdo con las propiedades
 * del servicio.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@ConfigurationProperties("services")
data class ServiceProperties(val profiles: Map<String, Profile> = HashMap()) {

  /**
   * Propiedades de configuración de un perfil.
   *
   * @param endpoint Endpoint de la solicitud saliente.
   * @param hostHeader Header que contiene el host de la solicitud saliente.
   * @param sendHeaders Conjunto de headers a reenviar desde la solicitud entrante hacia la
   * solicitud saliente.
   * @param headers Mapa de headers con sus valores para añadir a la solicitud saliente.
   * @param params Mapa de parámetros para añadir a la solicitud saliente; se pueden indicar beans
   * por su nombre, si Spring encuentra un bean de tipo [ServicePropertySupplier], se resuelve el
   * valor del parámetro.
   */
  data class Profile(
      val endpoint: String = "",
      val hostHeader: String = "",
      val sendHeaders: Set<String> = emptySet(),
      val headers: Map<String, String> = emptyMap(),
      val params: Map<String, String> = emptyMap(),
  ) {

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
      )
    }
  }

  companion object {
    /** Nombre del perfil considerado como fallback. */
    const val DEFAULT_PROFILE = "default"
  }
}
