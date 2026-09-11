package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.domain.Completion

data class MoodleActivityCompletion(
  val cmid: Int,
  val modname: String,
  val instance: Int,
  val state: Int,
  val timecompleted: Long,
  val tracking: Int,
  val overrideby: Int?,
  val valueused: Boolean?,
  val hascompletion: Boolean?,
  val isautomatic: Boolean?,
  val istrackeduser: Boolean?,
  val uservisible: Boolean?,
  val isoverallcomplete: Boolean?,
  val details: List<MoodleRuleDetail>?,
) {
  fun toCompletion() =
    Completion(
      cmid = cmid,
      module = modname,
      instance = instance,
      state = state,
      completedTs = timecompleted,
      tracking = tracking,
      overrideBy = overrideby,
      isValueUsed = valueused == true,
      isEnabled = hascompletion == true,
      isAutomatic = isautomatic == true,
      isTrackedUser = istrackeduser == true,
      isUserVisible = uservisible == true,
      isOverallComplete = isoverallcomplete == true,
    )
}
