package es.ubu.lsi.ubumonitorweb.domain

data class SiteConfig(
  val siteName: String,
  val siteLogo: String?,
  val loginType: Int,
  val loginLaunchUrl: String,
  val loginInstructions: String?,
  val maintenanceMessage: String,
  val isMaintenanceEnabled: Boolean,
  val isLoginFormEnabled: Boolean,
  val isLoginByEmailEnabled: Boolean,
  val isMobileServiceEnabled: Boolean,
  val isMfaEnabled: Boolean,
  val idProviders: List<IdProvider>,
  val wwwRoot: String,
  val wwwRootSecure: String,
)
