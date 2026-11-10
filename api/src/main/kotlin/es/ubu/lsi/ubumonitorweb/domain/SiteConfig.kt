/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

data class SiteConfig(
  val siteName: String,
  val siteLogo: String?,
  val loginStatus: LoginStatus,
  val loginUrl: String?,
  val loginInstructions: String?,
  val maintenanceMessage: String?,
)
