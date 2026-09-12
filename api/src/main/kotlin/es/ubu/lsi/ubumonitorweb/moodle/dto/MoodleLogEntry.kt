/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

data class MoodleLogEntry(
  val time: String,
  val userfullname: String,
  val affecteduser: String,
  val eventcontext: String,
  val component: String,
  val eventname: String,
  val description: String,
  val origin: String,
  val ipaddress: String,
)
