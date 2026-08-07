package es.ubu.lsi.ubumonitorweb.feature.dto

data class NormalizedCourse(
  val id: Int,
  val fullname: String,
  val summary: String?,
  val summaryformat: Int?,
  val courseimage: String?,
  val isfavourite: Boolean?,
  val startdate: Long?,
  val enddate: Long?,
  val category: MoodleCategory,
)
