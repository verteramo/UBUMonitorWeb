package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.LoginStatus
import es.ubu.lsi.ubumonitorweb.domain.Passport
import es.ubu.lsi.ubumonitorweb.domain.SiteConfig
import es.ubu.lsi.ubumonitorweb.moodle.client.ToolMobileClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodlePublicConfig
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

@Service
@ImportHttpServices(
  ToolMobileClient::class,
)
class SiteService(
  private val toolMobileClient: ToolMobileClient,
) {
  companion object {
    private const val URL_TEMPLATE: String = "%s?service=moodle_mobile_app&passport=%s"
  }

  /**
   * Passport actualmente activo generado por discover.
   */
  var passport: Passport? = null

  /**
   * Determina el modo de acceso disponible para el sitio.
   */
  private val MoodlePublicConfig.loginStatus: LoginStatus
    get() =
      when { // Comprobación de bloqueos
        maintenanceenabled == 1 -> LoginStatus.MAINTENANCE

        enablemobilewebservice != 1 -> LoginStatus.DISABLED

        // Comprobación de métodos habilitados
        typeoflogin > 1 || tool_mfa_enabled == true -> LoginStatus.SSO

        typeoflogin == 1 && showloginform == 1 -> LoginStatus.CREDENTIALS

        // Fallback
        else -> LoginStatus.ALL
      }

  /**
   * URL del sitio.
   */
  private val MoodlePublicConfig.siteUrl: String
    get() = httpswwwroot.ifBlank { wwwroot }

  /**
   * URL de inicio de sesión generada si el sitio admite SSO.
   */
  private val MoodlePublicConfig.loginUrl: String?
    get() =
      if (loginStatus in listOf(LoginStatus.SSO, LoginStatus.ALL) && launchurl.isNotBlank()) {
        val newPassport = Passport(siteUrl)
        passport = newPassport

        URL_TEMPLATE.format(launchurl, newPassport.uuid)
      } else {
        null
      }

  /**
   * Instrucciones de login.
   */
  private val MoodlePublicConfig.loginInstructions: String?
    get() =
      authinstructions.takeIf {
        it.isNotBlank() && loginStatus in listOf(LoginStatus.SSO, LoginStatus.CREDENTIALS, LoginStatus.ALL)
      }

  /**
   * Mensaje de mantenimiento.
   */
  private val MoodlePublicConfig.maintenanceMessage: String?
    get() =
      maintenancemessage.takeIf {
        it.isNotBlank() && loginStatus == LoginStatus.MAINTENANCE
      }

  /**
   * Mapping de MoodlePublicConfig a SiteConfig.
   */
  private fun MoodlePublicConfig.toSiteConfig(): SiteConfig =
    SiteConfig(
      siteName = sitename,
      siteLogo = compactlogourl,
      loginStatus = loginStatus,
      loginUrl = loginUrl,
      loginInstructions = loginInstructions,
      maintenanceMessage = maintenanceMessage,
    )

  /**
   * Descubre y extrae la configuración pública del sitio Moodle.
   *
   * @return Configuración del sitio o null si no está disponible.
   */
  fun discover(): SiteConfig? =
    toolMobileClient
      .getPublicConfig()
      .firstOrNull()
      ?.data
      ?.toSiteConfig()
}
