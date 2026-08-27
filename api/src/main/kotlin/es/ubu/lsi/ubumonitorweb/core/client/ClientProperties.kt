/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Propiedades de configuración de los clientes.
 */
@ConfigurationProperties("client")
data class ClientProperties(
  val profiles: Map<String, Profile> = emptyMap(),
) {
  /** Propiedades de configuración de los perfiles. */
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
    /** Propiedades de configuración de los proveedores. */
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
        host = host ?: parent.host,
        params = parent.params + params,
        headers = parent.headers + headers,
        cookies = parent.cookies + cookies,
        providers = parent.providers + providers,
        forwardHeaders = parent.forwardHeaders union forwardHeaders,
      )
  }

  init {
    profiles.forEach { (name, profile) ->
      profile.inherit.takeIf { it.isNotBlank() && it !in profiles }?.let { parent ->
        error(Message.ERROR_PROFILE_INHERIT(name, parent))
      }
    }
  }

  operator fun get(
    name: String,
    visited: Set<String> = emptySet(),
  ): Profile? =
    name.takeUnless { it.isBlank() || it in visited }?.let { profiles[it] }?.let {
      get(it.inherit, visited + name)?.let { parent -> it merge parent } ?: it
    }
}
