package es.ubu.lsi.ubumonitorweb.data.api

data class UserGrade(
  val courseId: Long,
  val userId: Long,
  val grades: List<GradeItem>,
)
