package es.ubu.lsi.ubumonitorweb.feature.course.api

import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCategory

/**
 * Objeto de curso normalizado.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
data class Course(
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
