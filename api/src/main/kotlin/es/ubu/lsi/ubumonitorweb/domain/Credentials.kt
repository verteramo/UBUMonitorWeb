/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

data class Credentials(
  val token: String,
  val privateToken: String,
  val sessionKey: String,
  val sessionCookie: String,
)
