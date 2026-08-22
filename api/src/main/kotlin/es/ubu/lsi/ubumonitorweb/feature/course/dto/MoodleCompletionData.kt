package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleCompletionData(
  val state: Int,
  val timecompleted: Long,
  val overrideby: Int? = null,
  val valueused: Boolean,
  val hascompletion: Boolean,
  val isautomatic: Boolean,
  val istrackeduser: Boolean,
  val uservisible: Boolean,
  val details: List<MoodleCompletionDetail>,
  val isoverallcomplete: Boolean,
)
