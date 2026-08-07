package es.ubu.lsi.ubumonitorweb.feature.dto

data class MoodleUserCourse(
  override val id: Int,
  override val fullname: String,
  override val summary: String?,
  override val summaryformat: Int?,
  override val courseimage: String?,
  override val isfavourite: Boolean?,
  override val startdate: Long?,
  override val enddate: Long?,
  // TODOS: core_enrol_get_users_courses
  val category: Int?,
) : Course
