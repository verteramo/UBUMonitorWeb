package es.ubu.lsi.ubumonitorweb.feature.course.dto

/**
 * Curso tal cual lo entrega el webservice de Moodle.
 *
 * La función `core_enrol_get_users_courses` entrega la categoría
 * como: `category: Int?`, que representa su identificador;
 *
 * Las funciones:
 * - `core_course_get_recent_courses`
 * - `block_starredcourses_get_starred_courses`
 * - `core_course_get_enrolled_courses_by_timeline_classification`
 * Entregan la categoría como: `coursecategory: String`, que representa su nombre.
 *
 * @author Marcelo Verteramo Pérsico
 */
data class MoodleCourse(
  val id: Int,
  val fullname: String,
  val summary: String?,
  val summaryformat: Int?,
  val courseimage: String?,
  val isfavourite: Boolean?,
  val startdate: Long?,
  val enddate: Long?,
  /*
   * Todos: core_enrol_get_users_courses
   */
  val category: Int?,
  /*
   * Recientes: core_course_get_recent_courses
   * Destacados: block_starredcourses_get_starred_courses
   * En progreso, Futuros, Pasados: core_course_get_enrolled_courses_by_timeline_classification
   */
  val coursecategory: String?,
)
