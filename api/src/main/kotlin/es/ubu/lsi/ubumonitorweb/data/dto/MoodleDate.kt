/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

data class MoodleDate(
  val label: String,
  val timestamp: Long,
  val relativeto: Long?,
  val dataid: String?,
) {
  fun sumTimestamps(): Long = this.timestamp + (this.relativeto ?: 0)
}
