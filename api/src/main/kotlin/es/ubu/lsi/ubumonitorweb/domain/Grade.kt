/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

data class Grade(
  val courseId: Long,
  val userId: Long,
  val grades: List<GradeItem>,
)
