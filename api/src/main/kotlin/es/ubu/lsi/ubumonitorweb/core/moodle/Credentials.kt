/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.moodle

/**
 * Objeto de credenciales de Moodle.
 */
data class Credentials(
  val token: String,
  val privatetoken: String,
)
