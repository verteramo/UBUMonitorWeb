package es.ubu.lsi.ubumonitorweb.domain

data class Grade(
  val courseId: Long,
  val userId: Long,
  val grades: List<GradeItem>,
)
