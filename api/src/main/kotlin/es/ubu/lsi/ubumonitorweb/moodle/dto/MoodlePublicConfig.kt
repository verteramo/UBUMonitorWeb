package es.ubu.lsi.ubumonitorweb.moodle.dto

data class MoodlePublicConfig(
  val sitename: String,
  val compactlogourl: String?,
  val typeoflogin: Int,
  val launchurl: String,
  val authinstructions: String?,
  val maintenancemessage: String,
  val maintenanceenabled: Int,
  val showloginform: Int?,
  val authloginviaemail: Int,
  val enablemobilewebservice: Int,
  val tool_mfa_enabled: Boolean?,
  val identityproviders: List<MoodleIdentityProvider>?,
  val wwwroot: String,
  val httpswwwroot: String,
)
