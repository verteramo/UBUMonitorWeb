/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

data class Principal(
  val id: Int,
  val username: String,
  val firstName: String,
  val lastName: String,
  val fullName: String,
  val picture: String?,
  val language: String,
  val timezone: String?,
  val isAdmin: Boolean,
  val siteUrl: String,
  val siteName: String,
  val siteVersion: String?,
  val siteRelease: String?,
  val siteTimezone: String,
)
