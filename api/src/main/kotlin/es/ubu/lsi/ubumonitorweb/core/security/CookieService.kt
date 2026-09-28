package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.select
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import es.ubu.lsi.ubumonitorweb.moodle.client.ToolMobileClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.hc.client5.http.cookie.Cookie
import org.apache.hc.client5.http.cookie.CookieStore
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices
import java.net.URI

/**
 * Servicio encargado de gestionar, validar y persistir las cookies de sesión de Moodle.
 *
 * @property cookieRepository Repositorio de persistencia de cookies.
 * @property toolMobileClient Cliente para servicios móviles y autologin de Moodle.
 * @property loginClient Cliente web para verificar la validez de la sesión.
 * @property cookieStore Almacén global de cookies HTTP.
 */
@Service
@ImportHttpServices(
  ToolMobileClient::class,
  LoginClient::class,
)
class CookieService(
  private val cookieRepository: CookieRepository,
  private val toolMobileClient: ToolMobileClient,
  private val loginClient: LoginClient,
  private val cookieStore: CookieStore,
) {
  private val logger = KotlinLogging.logger {}

  /**
   * Asegura la disponibilidad de una cookie de sesión válida en el almacén global.
   *
   * @param moodleToken Credenciales de acceso de Moodle.
   * @param userId Identificador único del usuario.
   * @throws AuthenticationServiceException Si no es posible obtener o estabilizar la cookie.
   */
  fun getOrFetchCookie(
    moodleToken: MoodleToken,
    userId: Int,
  ) {
    cookieStore.clear()

    cookieRepository.readCookie(moodleToken.token)?.takeIf { isValid(it) }?.also {
      logger.info { "Cookie loaded from repository is valid" }
    } ?: run {
      logger.info { "Cookie is missing, invalid or read failed" }

      autologin(moodleToken, userId).let { newCookie ->
        cookieRepository.writeCookie(newCookie, moodleToken.token)
        logger.info { "New cookie saved to repository" }
      }
    }
  }

  /**
   * Comprueba si una cookie recuperada del repositorio sigue siendo válida.
   *
   * @param cookie Objeto con la información de la cookie almacenada.
   * @return `true` si la cookie es válida; `false` en caso contrario.
   */
  private fun isValid(cookie: CookieRepository.Cookie): Boolean {
    cookieStore.clear()

    cookieStore.addCookie(
      cookie.run {
        BasicClientCookie(name, value).also {
          it.domain = domain
          it.path = path
          it.isSecure = isSecure
          it.setAttribute(Cookie.DOMAIN_ATTR, domain)
          it.setAttribute(Cookie.PATH_ATTR, path)
          if (isSecure) it.setAttribute(Cookie.SECURE_ATTR, "true")
        }
      },
    )

    return loginClient.getUserEditForm().select("body#page-user-edit").isNotEmpty()
  }

  /**
   * Ejecuta el flujo de autologin para obtener una nueva cookie.
   *
   * @param moodleToken Credenciales de acceso de Moodle.
   * @param userId Identificador único del usuario.
   * @return Cookie nueva extraída del store.
   *
   * @throws AuthenticationServiceException Si falla la obtención de la cookie de sesión.
   */
  private fun autologin(
    moodleToken: MoodleToken,
    userId: Int,
  ): CookieRepository.Cookie {
    cookieStore.clear()

    logger.info { "Starting autologin sequence (tool_mobile_get_autologin_key)" }

    toolMobileClient.getAutologinKey(moodleToken.token, moodleToken.privatetoken).let {
      logger.debug { "Autologin key: '$it'" }
      toolMobileClient.autologin(URI(it.autologinurl), it.key, userId)
    }

    return cookieStore.cookies.firstOrNull { it.name.startsWith("MoodleSession") }?.let {
      logger.info { "Session cookie obtained" }
      logger.debug { "Cookie: '$it'" }

      CookieRepository.Cookie(
        name = it.name,
        value = it.value,
        domain = it.domain,
        path = it.path,
        isSecure = it.isSecure,
      )
    } ?: run {
      logger.warn { "Error obtaining session cookie during autologin" }
      throw AuthenticationServiceException("Session cookie is not available")
    }
  }
}
