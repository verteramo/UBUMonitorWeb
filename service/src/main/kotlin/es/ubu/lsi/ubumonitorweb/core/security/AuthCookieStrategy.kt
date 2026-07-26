package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.feature.service.MoodleToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service

/**
 * Estrategia de autenticación mediante cookie.
 *
 * @param codec Códec.
 * @param properties Propiedades de configuración.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Service
@ConditionalOnProperty("security.strategy", havingValue = "cookie")
class AuthCookieStrategy(
    codec: AuthCodec<MoodleToken>,
    private val properties: Properties,
) : AuthStrategy<MoodleToken>(codec) {

  /**
   * Propiedades de configuración.
   *
   * @param name Nombre de la cookie.
   * @param path Ruta donde la cookie es válida.
   * @param httpOnly Si es true, impide el acceso a la cookie desde JavaScript (XSS).
   * @param sameSite Política de envío cruzado para protección contra ataques CSRF.
   * @param maxAge Tiempo máximo de vida de la cookie (segundos).
   */
  @ConfigurationProperties("security.strategy.cookie")
  data class Properties(
      val name: String,
      val path: String,
      val httpOnly: Boolean,
      val sameSite: Cookie.SameSite,
      val maxAge: Long,
  ) {

    /**
     * Construye y obtiene una cookie lista para usar a partir de las propiedades definidas en la
     * configuración.
     *
     * @param value Valor de la cookie.
     * @return Cookie.
     */
    fun getCookie(value: String): ResponseCookie {
      return ResponseCookie
          .from(name)
          .path(path)
          .maxAge(maxAge)
          .httpOnly(httpOnly)
          .sameSite(sameSite.attributeValue())
          .value(value)
          .build()
    }
  }

  /** Datos crudos de autenticación presentes en la cookie. */
  override val HttpServletRequest.data: String?
    get() {
      return cookies?.find { it.name == properties.name }?.value
    }

  /**
   * Inyecta los datos crudos de autenticación en la cookie.
   *
   * @param data Datos crudos de autenticación.
   */
  override fun HttpServletResponse.doRespond(data: String) {
    addHeader(HttpHeaders.SET_COOKIE, properties.getCookie(data).toString())
  }
}
