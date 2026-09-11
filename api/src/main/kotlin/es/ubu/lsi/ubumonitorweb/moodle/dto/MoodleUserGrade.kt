package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.domain.Grade

data class MoodleUserGrade(
  val courseid: Long,
  val courseidnumber: String?,
  val userid: Long,
  val userfullname: String,
  val useridnumber: String?,
  val maxdepth: Int,
  val gradeitems: List<MoodleGradeItem>,
) {
  fun toGrade() =
    Grade(
      userId = userid,
      courseId = courseid,
      grades = gradeitems.map { it.toGradeItem() },
    )
}
