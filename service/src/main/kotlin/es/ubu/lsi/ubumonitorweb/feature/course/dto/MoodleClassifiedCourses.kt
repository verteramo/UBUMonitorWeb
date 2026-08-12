package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleClassifiedCourses(
  val courses: List<MoodleCourse>,
  val warnings: List<MoodleCourseWarning>?,
)
