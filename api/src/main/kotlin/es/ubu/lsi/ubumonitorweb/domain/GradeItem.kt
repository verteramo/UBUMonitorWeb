/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

data class GradeItem(
  val id: Int,
  val cmid: Int?,
  val name: String?,
  val type: String,
  val module: String?,
  val instance: Int,
  val number: Int?,
  val category: Int?,
  val weight: Double?,
  val value: Double?,
  val minValue: Double?,
  val maxValue: Double?,
  val isUserLocked: Boolean,
  val isLocked: Boolean,
  val isHidden: Boolean,
  val isOverridden: Boolean,
  val needsUpdate: Boolean,
  val isHiddenByDate: Boolean,
  val submittedTs: Long?,
  val gradedTs: Long?,
  val status: String?,
  val feedback: String?,
)
