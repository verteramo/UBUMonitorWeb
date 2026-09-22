package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.domain.AuthConfig
import es.ubu.lsi.ubumonitorweb.domain.IdProvider

data class MoodlePublicConfig(
  val sitename: String,
  val compactlogourl: String?,
  val typeoflogin: Int,
  val launchurl: String,
  val authinstructions: String?,
  val maintenancemessage: String,
  val maintenanceenabled: Int,
  val showloginform: Int,
  val authloginviaemail: Int,
  val enablemobilewebservice: Int,
  val tool_mfa_enabled: Boolean,
  val identityproviders: List<MoodleIdentityProvider>?,
) {
  fun toAuthConfig() =
    AuthConfig(
      siteName = sitename,
      siteLogo = compactlogourl,
      loginType = typeoflogin,
      loginUrl = launchurl,
      loginInstructions = authinstructions,
      maintenanceMessage = maintenancemessage,
      isMaintenanceEnabled = maintenanceenabled == 1,
      isLoginFormEnabled = showloginform == 1,
      isLoginByEmailEnabled = authloginviaemail == 1,
      isMobileServiceEnabled = enablemobilewebservice == 1,
      isMfaEnabled = tool_mfa_enabled,
      idProviders =
        identityproviders?.map {
          IdProvider(
            url = it.url,
            name = it.name,
            icon = it.iconurl,
          )
        } ?: emptyList(),
    )
}
