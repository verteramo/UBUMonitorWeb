package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.crypto.CipherStrategy
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService.Companion.asString
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
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.net.URI
import kotlin.jvm.optionals.getOrNull

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
  private val mapper: ObjectMapper,
  private val cookieRepository: CookieRepository,
  private val digestService: DigestService,
  private val cipherStrategy: CipherStrategy,
  private val toolMobileClient: ToolMobileClient,
  private val loginClient: LoginClient,
  private val cookieStore: CookieStore,
) {
  data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val isSecure: Boolean,
  )

  private val logger = KotlinLogging.logger {}

  /**
   * Asegura la disponibilidad de una cookie de sesión válida en el almacén global.
   *
   * @param moodleToken Credenciales de acceso de Moodle.
   * @param resolveUserId Función que resuelve el identificador único del usuario.
   * @throws AuthenticationServiceException Si no es posible obtener o estabilizar la cookie.
   */
  fun getOrFetchCookie(
    moodleToken: MoodleToken,
    resolveUserId: () -> Int,
  ) {
    cookieStore.clear()

    val tokenDigest = digestService.digest(moodleToken.token)
    val tokenHash = tokenDigest.asString()

    val storedCookie =
      cookieRepository.findById(tokenHash).getOrNull()?.data?.let {
        runCatching {
          mapper.readValue<SerializableCookie>(
            cipherStrategy.decrypt(it, tokenDigest),
          )
        }.getOrNull()
      }

    if (storedCookie == null || !isValid(storedCookie)) {
      val newCookie = autologin(moodleToken, resolveUserId())
      val bytes = mapper.writeValueAsBytes(newCookie)
      val encryptedBytes = cipherStrategy.encrypt(bytes, tokenDigest)
      val cookieEntity =
        CookieEntity().apply {
          id = tokenHash
          data = encryptedBytes
        }

      cookieRepository.save(cookieEntity)
      logger.info { "New cookie securely saved to DB" }
    }

    logger.info { "Cookie loaded from DB is valid" }
  }

  /**
   * Comprueba si una cookie recuperada del repositorio sigue siendo válida.
   *
   * @param cookie Objeto con la información de la cookie almacenada.
   * @return `true` si la cookie es válida; `false` en caso contrario.
   */
  private fun isValid(cookie: SerializableCookie): Boolean {
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
  ): SerializableCookie {
    cookieStore.clear()

    logger.info { "Starting autologin sequence (tool_mobile_get_autologin_key)" }

    toolMobileClient.getAutologinKey(moodleToken.token, moodleToken.privatetoken).let {
      logger.debug { "Autologin key: '$it'" }
      toolMobileClient.autologin(URI(it.autologinurl), it.key, userId)
    }

    return cookieStore.cookies.firstOrNull { it.name.startsWith("MoodleSession") }?.let {
      logger.info { "Session cookie obtained" }
      logger.debug { "Cookie: '$it'" }

      SerializableCookie(
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
