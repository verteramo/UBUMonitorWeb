package es.ubu.lsi.ubumonitorweb.feature.course.api

/**
 * Objeto de curso normalizado.
 *
 * @author Marcelo Verteramo Pérsico
 */
data class Course(
  val id: Int,
  val name: String,
  val picture: String?,
  val starred: Boolean,
  val startDate: Long?,
  val endDate: Long?,
  val category: String,
)
