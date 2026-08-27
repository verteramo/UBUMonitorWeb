package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleDate(
  val label: String,
  val timestamp: Long,
  val relativeto: Long?,
  val dataid: String?,
)
