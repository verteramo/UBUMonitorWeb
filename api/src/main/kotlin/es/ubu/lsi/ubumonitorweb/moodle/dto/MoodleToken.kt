/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

/**
 * Objeto de credenciales de Moodle.
 */
data class MoodleToken(
  val token: String,
  val privatetoken: String,
)
