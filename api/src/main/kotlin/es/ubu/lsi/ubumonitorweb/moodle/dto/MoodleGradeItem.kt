/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.domain.GradeItem

data class MoodleGradeItem(
  val id: Int,
  val itemname: String?,
  val itemtype: String,
  val itemmodule: String?,
  val iteminstance: Int,
  val itemnumber: Int?,
  val idnumber: String?,
  val categoryid: Int?,
  val outcomeid: Int?,
  val scaleid: Int?,
  val locked: Boolean?,
  val cmid: Int?,
  val weightraw: Double?,
  val weightformatted: String?,
  val status: String?,
  val graderaw: Double?,
  val gradedatesubmitted: Long?,
  val gradedategraded: Long?,
  val gradehiddenbydate: Boolean?,
  val gradeneedsupdate: Boolean?,
  val gradeishidden: Boolean?,
  val gradeislocked: Boolean?,
  val gradeisoverridden: Boolean?,
  val gradeformatted: String?,
  val grademin: Double?,
  val grademax: Double?,
  val rangeformatted: String?,
  val percentageformatted: String?,
  val feedback: String?,
  val feedbackformat: Int?,
) {
  fun toGradeItem() =
    GradeItem(
      id = id,
      cmid = cmid,
      name = itemname,
      type = itemtype,
      module = itemmodule,
      instance = iteminstance,
      number = itemnumber,
      category = categoryid,
      weight = weightraw,
      value = graderaw,
      minValue = grademin,
      maxValue = grademax,
      isUserLocked = locked == true,
      isLocked = gradeislocked == true,
      isHidden = gradeishidden == true,
      isOverridden = gradeisoverridden == true,
      needsUpdate = gradeneedsupdate == true,
      isHiddenByDate = gradehiddenbydate == true,
      submittedTs = gradedatesubmitted,
      gradedTs = gradedategraded,
      status = status,
      feedback = feedback,
    )
}
