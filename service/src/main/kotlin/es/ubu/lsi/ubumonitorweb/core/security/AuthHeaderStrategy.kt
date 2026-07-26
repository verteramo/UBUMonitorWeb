package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.feature.service.MoodleToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

/**
 * Estrategia de autenticación mediante header.
 *
 * @param codec Códec.
 * @param properties Propiedades de configuración.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Service
@ConditionalOnProperty("security.strategy", havingValue = "header")
class AuthHeaderStrategy(
    codec: AuthCodec<MoodleToken>,
    private val properties: Properties,
) : AuthStrategy<MoodleToken>(codec) {

  /**
   * Propiedades de configuración.
   *
   * @param scheme Esquema de autenticación (Basic, Bearer, Digest, ...).
   * @param key Nombre de la clave que contendrá el Principal.
   */
  @ConfigurationProperties("security.strategy.header")
  data class Properties(val scheme: String, val key: String)

  /** Datos crudos de autenticación presentes en el header. */
  override val HttpServletRequest.data: String?
    get() {
      return getHeader(HttpHeaders.AUTHORIZATION)
          .takeIf { it.contains(properties.scheme) }
          ?.removePrefix(properties.scheme)
          ?.trim()
    }

  /**
   * Inyecta los datos crudos de autenticación en el header.
   *
   * @param data Datos crudos de autenticación.
   */
  override fun HttpServletResponse.doRespond(data: String): Any? {
    return mapOf(properties.key to data)
  }
}
