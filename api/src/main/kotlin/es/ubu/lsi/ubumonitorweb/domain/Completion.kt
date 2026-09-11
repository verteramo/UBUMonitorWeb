/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

data class Completion(
  val cmid: Int,
  val module: String,
  val instance: Int,
  val state: Int?,
  val completedTs: Long?,
  val tracking: Int,
  val overrideBy: Int?,
  val isEnabled: Boolean,
  val isValueUsed: Boolean,
  val isAutomatic: Boolean,
  val isTrackedUser: Boolean,
  val isUserVisible: Boolean,
  val isOverallComplete: Boolean,
)
