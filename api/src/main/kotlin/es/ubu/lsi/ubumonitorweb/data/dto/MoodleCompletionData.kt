/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

data class MoodleCompletionData(
  val state: Int,
  val timecompleted: Long,
  val overrideby: Int?,
  val valueused: Boolean,
  val hascompletion: Boolean,
  val isautomatic: Boolean,
  val istrackeduser: Boolean,
  val uservisible: Boolean,
  val details: List<MoodleCompletionDetail>,
  val isoverallcomplete: Boolean?,
)
