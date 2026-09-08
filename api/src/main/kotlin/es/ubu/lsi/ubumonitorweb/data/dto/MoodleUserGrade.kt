package es.ubu.lsi.ubumonitorweb.data.dto

import es.ubu.lsi.ubumonitorweb.data.api.UserGrade

data class MoodleUserGrade(
  val courseid: Long,
  val courseidnumber: String?,
  val userid: Long,
  val userfullname: String,
  val useridnumber: String?,
  val maxdepth: Int,
  val gradeitems: List<MoodleGradeItem>,
) {
  fun toUserGrade() =
    UserGrade(
      userId = userid,
      courseId = courseid,
      grades = gradeitems.map { it.toGradeItem() },
    )
}
