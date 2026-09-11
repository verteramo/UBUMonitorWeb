package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.core.security.Credentials
import es.ubu.lsi.ubumonitorweb.core.security.Principal
import org.jsoup.Jsoup
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Servicio de autenticación que realiza todas las consultas necesarias a los distintos endpoints de Moodle para
 * recopilar toda la información necesaria para el contexto de Spring Security.
 */
@Service
@ImportHttpServices(
  LoginClient::class,
  TokenClient::class,
  CoreWebserviceClient::class,
  CoreUserClient::class,
  UserEditClient::class,
)
class AuthService(
  private val loginClient: LoginClient,
  private val tokenClient: TokenClient,
  private val coreWebserviceClient: CoreWebserviceClient,
  private val coreUserClient: CoreUserClient,
  private val userEditClient: UserEditClient,
) {
  /**
   * Mapea un token de Moodle, junto con la 'sesskey' y la cookie de sesión a un objeto Credentials.
   */
  private fun MoodleToken.toCredentials(
    key: String,
    cookie: String,
  ) = Credentials(
    token = token,
    privateToken = privatetoken,
    sessionKey = key,
    sessionCookie = cookie,
  )

  /**
   * Mapea los datos del Principal.
   */
  private fun MoodleSiteInfo.toPrincipal(
    timezone: String?,
    siteTimezone: String,
  ) = Principal(
    id = userid,
    username = username,
    isAdmin = userissiteadmin == true,
    language = lang,
    firstName = firstname,
    lastName = lastname,
    fullName = fullname,
    picture = userpictureurl,
    timezone = timezone,
    siteUrl = siteurl,
    siteName = sitename,
    siteVersion = version,
    siteRelease = release,
    siteTimezone = siteTimezone,
  )

  /**
   * Extrae la cookie de sesión de Moodle.
   */
  private val ResponseEntity<String>.moodleSessionCookie: String?
    get() = headers[HttpHeaders.SET_COOKIE]?.firstOrNull { it.startsWith("MoodleSession") }?.substringBefore(";")

  /**
   * Extrae el token CSRF `logintoken` desde el HTML del formulario de login de Moodle.
   */
  private val ResponseEntity<String>.loginToken: String?
    get() = body?.let { Jsoup.parse(it).selectFirst("input[name=logintoken]")?.attr("value") }

  /**
   * Extrae la `sesskey` desde el HTML del formulario de login de Moodle.
   * Esta clave sirve debe acompañar cualquier acción significativa:
   * https://github.com/moodle/moodle/blob/48ea8c33227277e916cdd4079dbecef448b37f2c/public/lib/sessionlib.php#L65
   */
  private val ResponseEntity<String>.sessionKey: String?
    get() =
      body?.let {
        Regex("sesskey=(\\w+)").find(it)?.groupValues?.get(1)
          ?: Regex("\"sesskey\":\"(\\w+)\"").find(it)?.groupValues?.get(1)
      }

  /**
   * Extrae la zona horaria del servidor desde el HTML del formulario de edición del perfil de Moodle.
   */
  private val ResponseEntity<String>.siteTimezone: String?
    get() =
      body?.let {
        Jsoup.parse(it).selectFirst("select#id_timezone option[value=99]")?.text()?.let {
          Regex("\\((.*)\\)").find(it)?.groupValues?.get(1)
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
  ): Credentials {
    // Llamada GET al formulario de login
    // Se extraen la primera cookie de sesión, el 'logintoken' y la 'sesskey'
    val getResponse = loginClient.getCall()
    val firstCookie = getResponse.moodleSessionCookie
    val loginToken = getResponse.loginToken
    val sessionKey = getResponse.sessionKey

    if (firstCookie is String && loginToken is String && sessionKey is String) {
      // Llamada POST al formulario de login
      // Se incluyen la cookie de sesión y el 'logintoken'
      val postResponse = loginClient.postCall(firstCookie, username, password, loginToken)
      // Se extrae la segunda cookie de sesión, se utiliza la primera como fallback
      val sessionCookie = postResponse.moodleSessionCookie ?: firstCookie
      // Solicitud del token de los webservices
      val moodleToken = tokenClient.getToken(username, password)

      return moodleToken.toCredentials(sessionKey, sessionCookie)
    }

    // En caso de no lograrse la extracción de alguno de todos estos datos
    // se puede determinar como un error de login inválido
    throw Message.ERROR_INVALID_LOGIN(HttpStatus.UNAUTHORIZED)
  }

  /**
   * Realiza todo el procedimiento necesario para obtener los datos del principal.
   */
  fun getPrincipal(credentials: Credentials): Principal? {
    val siteInfo = coreWebserviceClient.getSiteInfo(credentials.token)
    val userData = coreUserClient.getUsersByField("username", listOf(siteInfo.username)).firstOrNull()
    val siteTimezone = userEditClient.getEditForm().siteTimezone

    return siteTimezone?.let { siteInfo.toPrincipal(userData?.timezone, it) }
  }
}
