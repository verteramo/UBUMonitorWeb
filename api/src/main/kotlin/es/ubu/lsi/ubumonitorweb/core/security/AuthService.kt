package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.domain.AuthConfig
import es.ubu.lsi.ubumonitorweb.domain.Credentials
import es.ubu.lsi.ubumonitorweb.domain.Principal
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreUserClient
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreWebserviceClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import es.ubu.lsi.ubumonitorweb.moodle.client.TokenClient
import es.ubu.lsi.ubumonitorweb.moodle.client.ToolMobileClient
import es.ubu.lsi.ubumonitorweb.moodle.client.UserEditClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.registry.ImportHttpServices
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilderFactory
import java.time.ZoneId
import java.time.ZoneOffset

interface AutologinClient {
  @GetExchange
  fun autologin(
    url: UriBuilderFactory,
    @RequestParam key: String,
  ): ResponseEntity<String>
}

/**
 * Servicio de autenticación que realiza todas las consultas necesarias a los distintos endpoints de Moodle para
 * recopilar toda la información necesaria para el contexto de Spring Security.
 */
@Service
@ImportHttpServices(
  AutologinClient::class,
  ToolMobileClient::class,
  LoginClient::class,
  TokenClient::class,
  CoreWebserviceClient::class,
  CoreUserClient::class,
  UserEditClient::class,
)
class AuthService(
  private val autologinClient: AutologinClient,
  private val toolMobileClient: ToolMobileClient,
  private val loginClient: LoginClient,
  private val tokenClient: TokenClient,
  private val coreWebserviceClient: CoreWebserviceClient,
  private val coreUserClient: CoreUserClient,
  private val userEditClient: UserEditClient,
) {
  private val logger = KotlinLogging.logger {}

  /**
   * Extrae la cookie de sesión de Moodle.
   */
  private val ResponseEntity<String>.sessionCookie: String?
    get() = headers[HttpHeaders.SET_COOKIE]?.firstOrNull { it.startsWith("MoodleSession") }?.substringBefore(";")

  /**
   * Extrae la `sesskey` desde el HTML del formulario de login de Moodle.
   * Esta clave sirve debe acompañar cualquier acción significativa:
   * https://github.com/moodle/moodle/blob/48ea8c33227277e916cdd4079dbecef448b37f2c/public/lib/sessionlib.php#L65
   */
  private val ResponseEntity<String>.sessionKey: String?
    get() = body?.let { Regex("\"sesskey\":\"(\\w+)\"").find(it)?.groupValues?.get(1) }

  /**
   * Extrae el token CSRF `logintoken` desde el HTML del formulario de login de Moodle.
   */
  private val ResponseEntity<String>.loginToken: String?
    get() = body?.let { Jsoup.parse(it).selectFirst("input[name=logintoken]")?.attr("value") }

  /**
   * Extrae la zona horaria del sitio desde el HTML del formulario de edición del perfil de Moodle.
   */
  private val ResponseEntity<String>.siteTimezone: String?
    get() =
      body?.let { body ->
        Jsoup.parse(body).selectFirst("select#id_timezone option[value=\"99\"]")?.text()?.let {
          Regex("\\((.+)\\)").find(it)?.groupValues?.get(1)
        }
      }

  /**
   * Realiza todo el procedimiento necesario para obtener las credenciales para los diferentes accesos:
   *
   * Acceso mediante webservices:
   * - `token`
   * - `privatetoken`
   *
   * Acceso mediante webscraping:
   * - `sesskey`
   * - Cookie `MoodleSession`
   */
  fun getCredentials(
    username: String,
    password: String,
  ): Credentials { // Llamada GET al formulario de login
    // Se extraen la primera cookie de sesión, el 'logintoken' y la 'sesskey'
    val getResponse = loginClient.getCall()
    val firstCookie = getResponse.sessionCookie
    val sessionKey = getResponse.sessionKey
    val loginToken = getResponse.loginToken

    logger.debug {
      """
      Getting credentials:
      First cookie: $firstCookie
      Login token: $loginToken
      Session key: $sessionKey
      """.trimIndent()
    }

    if (firstCookie is String && loginToken is String && sessionKey is String) { // Llamada POST al formulario de login
      // Se incluyen la cookie de sesión y el 'logintoken'
      val postResponse =
        loginClient.postCall(
          firstCookie,
          username,
          password,
          loginToken,
        )

      // Se extrae la segunda cookie de sesión, se utiliza la primera como fallback
      val sessionCookie = postResponse.sessionCookie ?: firstCookie

      // Solicitud del token de los webservices
      val moodleToken = tokenClient.getToken(username, password)

      return moodleToken.toCredentials(sessionKey, sessionCookie)
    }

    // En caso de no lograrse la extracción de alguno de todos estos datos
    // se puede determinar como un error de login inválido
    throw Message.ERROR_INVALID_LOGIN(HttpStatus.UNAUTHORIZED)
  }

  fun getCredentials(token: String): Credentials {
    val decodedBytes =
      try {
        // Si la URL ha escapado caracteres, los decodificamos protegiendo los '+' del Base64
        val cleanToken =
          java.net.URLDecoder
            .decode(token.trimEnd('/'), Charsets.UTF_8.name())
            .replace(' ', '+')

        try {
          java.util.Base64
            .getDecoder()
            .decode(cleanToken)
        } catch (e: IllegalArgumentException) {
          // Fallback defensivo a Base64 URL-safe
          java.util.Base64
            .getUrlDecoder()
            .decode(cleanToken)
        }
      } catch (e: Exception) {
        throw IllegalArgumentException("Token SSO de Moodle inválido", e)
      }

    val parts = String(decodedBytes, Charsets.UTF_8).split(":::")
    if (parts.size < 2 || parts[1].isBlank()) {
      throw IllegalArgumentException("Token de WebService de Moodle faltante")
    }

    val moodleToken =
      MoodleToken(
        token = parts[1],
        privatetoken = parts[2],
      )

    val moodleAutologinKey = toolMobileClient.getAutologinKey(moodleToken.token, moodleToken.privatetoken)

    val autologinResponse =
      autologinClient.autologin(
        DefaultUriBuilderFactory(
          moodleAutologinKey.autologinurl,
        ),
        moodleAutologinKey.key,
      )

    val sessionKey = autologinResponse.sessionKey
    val sessionCookie = autologinResponse.sessionCookie

    if (sessionKey is String && sessionCookie is String) {
      return moodleToken.toCredentials(sessionKey, sessionCookie)
    }

    throw Message.ERROR_INVALID_LOGIN(HttpStatus.UNAUTHORIZED)
  }

  /**
   * Convierte una cadena en una zona horaria;
   * Moodle utiliza "99" como valor especial en la zona horaria
   * del usuario que indica que se utiliza la del servidor.
   */
  private fun String?.toZoneId(fallback: String): ZoneId =
    if (!isNullOrBlank() && !equals("99")) {
      runCatching { ZoneId.of(this) }.getOrNull() ?: runCatching {
        ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(toDouble().times(3600).toInt()))
      }.getOrNull()
    } else {
      null
    } ?: ZoneId.of(fallback)

  /**
   * Realiza todo el procedimiento necesario para obtener los datos del principal.
   */
  fun getPrincipal(credentials: Credentials): Principal =
    credentials.run {
      val siteInfo = coreWebserviceClient.getSiteInfo(token)
      val siteTimezone = userEditClient.getEditForm(sessionCookie).siteTimezone.toZoneId("UTC")
      val userTimezone =
        coreUserClient.getUsersByField(token, "username", listOf(siteInfo.username)).firstOrNull()?.timezone.toZoneId(
          siteTimezone.id,
        )

      siteInfo.toPrincipal(userTimezone, siteTimezone)
    }

  fun discover(): AuthConfig? {
    val moodlePublicConfig = toolMobileClient.getPublicConfig()
    return moodlePublicConfig.firstOrNull()?.data?.toAuthConfig()
  }
}
