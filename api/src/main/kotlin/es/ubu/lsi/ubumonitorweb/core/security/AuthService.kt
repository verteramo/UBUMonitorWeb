package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.locale.I18nService
import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.select
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.sesskey
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.siteTimezone
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.toZoneId
import es.ubu.lsi.ubumonitorweb.domain.Keyring
import es.ubu.lsi.ubumonitorweb.domain.User
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreUserClient
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreWebserviceClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import es.ubu.lsi.ubumonitorweb.moodle.client.ToolMobileClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleToken
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices
import java.net.URI

/**
 * Servicio de autenticación que realiza todas las consultas necesarias a los distintos endpoints de Moodle,
 * recopilando toda la información necesaria para el contexto de Spring Security.
 */
@Service
@ImportHttpServices(
  LoginClient::class,
  CoreUserClient::class,
  ToolMobileClient::class,
  CoreWebserviceClient::class,
)
class AuthService(
  private val i18n: I18nService,
  private val authManager: AuthenticationManager,
  private val userService: UserService,
  private val cookieService: CookieService,
  private val loginClient: LoginClient,
  private val coreUserClient: CoreUserClient,
  private val toolMobileClient: ToolMobileClient,
  private val coreWebserviceClient: CoreWebserviceClient,
) {
  private fun testlogin(cookie: UserEntity.Vault.Cookie): ResponseEntity<String>? {
    cookieService.addCookie(cookie)

    return loginClient.getUserEditForm().takeIf { page ->
      page.select("body#page-user-edit").isNotEmpty()
    }
  }

  private fun autologin(
    token: String,
    privateToken: String,
    userId: Int,
  ): ResponseEntity<String>? {
    cookieService.clearStore()

    return toolMobileClient.getAutologinKey(token, privateToken).let {
      toolMobileClient.autologin(URI(it.autologinurl), it.key, userId).let {
        loginClient.getUserEditForm().takeIf { page ->
          page.select("body#page-user-edit").isNotEmpty()
        }
      }
    }
  }

  /**
   * Autentica el token proporcionado, recupera los metadatos restantes de Moodle,
   * lee las preferencias locales del usuario y construye el token de autenticación definitivo.
   *
   * @param authentication Token con las credenciales iniciales.
   * @return Token de autenticación resultante con la entidad User completa como principal.
   */
  fun authenticate(authentication: Authentication): Triple<User, Keyring, Map<String, Any>> {
    cookieService.clearStore()

    // Los providers resuelven el MoodleToken(token, privatetoken)
    val moodleToken = authManager.authenticate(authentication).credentials as MoodleToken

    // Se obtiene el MoodleSiteInfo para acceder al userid
    val moodleSiteInfo = coreWebserviceClient.getSiteInfo(moodleToken.token)

    // Se obtiene el MoodleUser, mediante el userid, para obtener el email
    val moodleUser =
      coreUserClient.getUsersByField(moodleToken.token, "id", listOf(moodleSiteInfo.userid)).firstOrNull()

    val userEntity = userService.findOrCreate(moodleUser, moodleSiteInfo)

    val vault = userService.getVault(userEntity, moodleToken.token)

    val privateToken =
      moodleToken.privatetoken ?: vault?.privateToken
        ?: throw SessionConflictException(i18n(Message.ERROR_AUTH_TOKEN_MISSING))

    val editForm =
      vault?.cookie?.let { testlogin(it) }
        ?: autologin(moodleToken.token, privateToken, moodleSiteInfo.userid)
        ?: throw SessionConflictException(i18n(Message.ERROR_AUTH_LOGIN_FAILED))

    val sesskey =
      editForm.sesskey
        ?: throw SessionConflictException(i18n(Message.ERROR_AUTH_SESSKEY_MISSING))

    val cookie =
      cookieService.getCookie("MoodleSession")
        ?: throw SessionConflictException(i18n(Message.ERROR_AUTH_COOKIE_MISSING))

    val newVault = UserEntity.Vault(cookie, privateToken)

    userService.setVault(userEntity, newVault, moodleToken.token)
    userService.save(userEntity)

    val siteTimezone = editForm.siteTimezone ?: "UTC"
    val siteTimezoneId = siteTimezone.toZoneId("UTC")
    val userTimezoneId = moodleUser?.timezone?.toZoneId(siteTimezoneId.id) ?: siteTimezoneId

    val preferences = userService.getPreferences(userEntity)

    val user =
      User(
        id = moodleSiteInfo.userid,
        email = moodleUser?.email?.lowercase()?.trim(),
        username = moodleSiteInfo.username,
        firstName = moodleSiteInfo.firstname,
        lastName = moodleSiteInfo.lastname,
        fullName = moodleSiteInfo.fullname,
        picture = moodleSiteInfo.userpictureurl,
        language = moodleSiteInfo.lang,
        timezone = userTimezoneId,
        isAdmin = moodleSiteInfo.userissiteadmin == true,
        siteUrl = moodleSiteInfo.siteurl,
        siteName = moodleSiteInfo.sitename,
        siteVersion = moodleSiteInfo.version,
        siteRelease = moodleSiteInfo.release,
        siteTimezone = siteTimezoneId,
      )

    val keyring = Keyring(moodleToken.token, sesskey)

    return Triple(user, keyring, preferences)
  }
}
