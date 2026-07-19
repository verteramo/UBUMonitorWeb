package es.ubu.lsi.ubumonitorweb.core.rest

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Propiedades de configuración de los servicios de Moodle.
 *
 * En la versión anterior los servicios se configuraban con anotaciones, dando
 * lugar a configuraciones dispersas en el código fuente, algo con evidentes
 * desventajas en caso de querer realizar refactorizaciones, es por ello que en
 * esta versión, los metadatos de los servicios pasan a residir exclusivamente
 * en el fichero de configuración de la aplicación, bien `application.yaml` o
 * bien `application.properties`.
 *
 * De esta manera, los servicios ahora se anotan con
 *
 * ```kotlin
 * @MoodleService("key")
 * ```
 *
 * y el procesador de la anotación construye la solicitud saliente de acuerdo
 * con las propiedades del servicio.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@ConfigurationProperties("moodle")
data class MoodleProperties(val profiles: Map<String, Profile> = HashMap()) {

  data class Profile(
      val endpoint: String = "",
      val hostHeader: String = "",
      val sendHeaders: Set<String> = emptySet(),
      val headers: Map<String, String> = emptyMap(),
      val params: Map<String, String> = emptyMap(),
  ) {
    infix fun merge(profile: Profile) =
        Profile(
          endpoint = endpoint.ifBlank { profile.endpoint },
          hostHeader = hostHeader.ifBlank { profile.hostHeader },
          sendHeaders = sendHeaders union profile.sendHeaders,
          headers = profile.headers + headers,
          params = profile.params + params,
        )
  }

  companion object {
    const val DEFAULT_PROFILE = "default"
  }
}
