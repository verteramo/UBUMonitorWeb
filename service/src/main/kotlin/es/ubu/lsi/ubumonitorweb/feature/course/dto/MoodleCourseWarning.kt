package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleCourseWarning(
  val item: String?,
  val itemid: Int?,
  val warningcode: String,
  val message: String,
)
