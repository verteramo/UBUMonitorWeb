package es.ubu.lsi.ubumonitorweb.feature.course.dto

/**
 * Categoría de curso tal cual la entrega el webservice de Moodle.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
data class MoodleCategory(
  val id: Int,
  val name: String,
  val description: String,
  val descriptionformat: Int,
  val path: String,
)
