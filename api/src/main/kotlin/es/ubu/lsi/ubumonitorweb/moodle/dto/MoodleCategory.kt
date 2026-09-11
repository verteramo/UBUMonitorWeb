/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

data class MoodleCategory(
  val id: Int,
  val name: String,
  val description: String,
  val descriptionformat: Int,
  val path: String,
)
