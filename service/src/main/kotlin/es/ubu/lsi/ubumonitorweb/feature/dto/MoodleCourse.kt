package es.ubu.lsi.ubumonitorweb.feature.dto

data class MoodleCourse(
  override val id: Int,
  override val fullname: String,
  override val summary: String?,
  override val summaryformat: Int?,
  override val courseimage: String?,
  override val isfavourite: Boolean?,
  override val startdate: Long?,
  override val enddate: Long?,
  // RECIENTES: core_course_get_recent_courses
  // DESTACADOS: block_starredcourses_get_starred_courses
  // EN PROGRESO, FUTUROS, PASADOS: core_course_get_enrolled_courses_by_timeline_classification
  val coursecategory: String,
) : Course
