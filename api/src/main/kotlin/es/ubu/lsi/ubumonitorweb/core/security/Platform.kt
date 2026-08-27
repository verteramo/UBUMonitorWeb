/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

data class Platform(
  val url: String,
  val name: String,
  val version: String?,
  val release: String?,
)
