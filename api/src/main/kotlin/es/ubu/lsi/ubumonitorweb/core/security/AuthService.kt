package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.siteTimezone
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.toZoneId
import es.ubu.lsi.ubumonitorweb.core.system.SystemProperties
import es.ubu.lsi.ubumonitorweb.domain.Keychain
import es.ubu.lsi.ubumonitorweb.domain.LoginMode
import es.ubu.lsi.ubumonitorweb.domain.LoginStatus
import es.ubu.lsi.ubumonitorweb.domain.Passport
import es.ubu.lsi.ubumonitorweb.domain.Preferences
import es.ubu.lsi.ubumonitorweb.domain.SiteConfig
import es.ubu.lsi.ubumonitorweb.domain.User
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreUserClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import es.ubu.lsi.ubumonitorweb.moodle.client.ToolMobileClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleSiteInfo
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices
import java.net.URLEncoder

/**
 * Servicio de autenticación que realiza todas las consultas necesarias a los distintos endpoints de Moodle para
 * recopilar toda la información necesaria para el contexto de Spring Security.
 */
@Service
@ImportHttpServices(
  ToolMobileClient::class,
  LoginClient::class,
  CoreUserClient::class,
)
class AuthService(
  private val authManager: AuthenticationManager,
  private val toolMobileClient: ToolMobileClient,
  private val loginClient: LoginClient,
  private val coreUserClient: CoreUserClient,
  private val preferencesRepository: PreferencesRepository,
  private val systemProperties: SystemProperties,
) {
  /**
   * Passport actualmente activo generado por discover.
   */
  private var activePassport: Passport? = null

  /**
   * Descubre y extrae la configuración pública del sitio Moodle.
   *
   * @return Configuración del sitio o null si no está disponible.
   */
  fun discover(scheme: String): SiteConfig? =
    toolMobileClient.getPublicConfig().firstOrNull()?.data?.run {
      val loginStatus: LoginStatus =
        when {
          maintenanceenabled == 1 -> LoginStatus.MAINTENANCE
          enablemobilewebservice != 1 -> LoginStatus.DISABLED
          else -> LoginStatus.ENABLED
        }

      val loginMode: LoginMode? =
        when {
          loginStatus != LoginStatus.ENABLED -> null
          typeoflogin > 1 || tool_mfa_enabled == true -> LoginMode.SSO
          typeoflogin == 1 && showloginform == 1 -> LoginMode.CREDENTIALS
          else -> LoginMode.ALL
        }

      val siteUrl = httpswwwroot.ifBlank { wwwroot }

      val loginUrl =
        if (loginStatus == LoginStatus.ENABLED && loginMode != LoginMode.CREDENTIALS && launchurl.isNotBlank()) {
          val newPassport = Passport(siteUrl)
          activePassport = newPassport

          systemProperties.ssoUrlTemplate.format(
            launchurl,
            newPassport.uuid,
            URLEncoder.encode(scheme, Charsets.UTF_8),
          )
        } else {
          null
        }

      val loginInstructions =
        authinstructions.takeIf {
          loginStatus == LoginStatus.ENABLED && !it.isNullOrBlank()
        }

      val maintenanceMessage =
        maintenancemessage.takeIf {
          loginStatus == LoginStatus.MAINTENANCE && it.isNotBlank()
        }

      SiteConfig(
        siteName = sitename,
        siteLogo = compactlogourl,
        loginStatus = loginStatus,
        loginMode = loginMode,
        loginUrl = loginUrl,
        loginInstructions = loginInstructions,
        maintenanceMessage = maintenanceMessage,
      )
    }

  /**
   * Autentica el token proporcionado, recupera los metadatos restantes de Moodle,
   * lee las preferencias locales del usuario y construye el token de autenticación definitivo.
   *
   * @param authentication Token con las credenciales iniciales.
   * @return Token de autenticación resultante con la entidad User completa como principal.
   */
  fun authenticate(authentication: Authentication): Authentication { // Validación de credenciales delegada a los AuthProviders
    val authResult = authManager.authenticate(authentication)
    val siteInfo = authResult.principal as MoodleSiteInfo
    val keychain = authResult.credentials as Keychain

    // Resolución de zonas horarias
    val siteTimezone = loginClient.getUserEditForm().siteTimezone.toZoneId("UTC")
    val userTimezone =
      coreUserClient.getUsersByField(keychain.token, "id", listOf(siteInfo.userid)).firstOrNull()?.timezone.toZoneId(
        siteTimezone.id,
      )

    // Recuperación de las preferencias del usuario desde el almacenamiento local
    val preferences = preferencesRepository.readPreferences(siteInfo.siteurl, siteInfo.userid)

    // Construcción del objeto de dominio con la información del usuario
    val user =
      User(
        id = siteInfo.userid,
        username = siteInfo.username,
        firstName = siteInfo.firstname,
        lastName = siteInfo.lastname,
        fullName = siteInfo.fullname,
        picture = siteInfo.userpictureurl,
        language = siteInfo.lang,
        timezone = userTimezone,
        isAdmin = siteInfo.userissiteadmin == true,
        siteUrl = siteInfo.siteurl,
        siteName = siteInfo.sitename,
        siteVersion = siteInfo.version,
        siteRelease = siteInfo.release,
        siteTimezone = siteTimezone,
        preferences = preferences,
      )

    return UsernamePasswordAuthenticationToken(user, keychain, emptyList())
  }

  /**
   * Actualiza y persiste las preferencias del usuario autenticado en el disco local.
   *
   * @param user Usuario autenticado actual.
   * @param newPreferences Nuevas preferencias enviadas desde la interfaz.
   * @return Instancia de [User] actualizada con las nuevas preferencias.
   */
  fun updatePreferences(
    user: User,
    newPreferences: Preferences,
  ): User {
    preferencesRepository.writePreferences(newPreferences, user.siteUrl, user.id)
    return user.copy(preferences = newPreferences)
  }
}
