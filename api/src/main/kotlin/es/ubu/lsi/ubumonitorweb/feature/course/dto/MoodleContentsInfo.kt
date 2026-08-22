package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleContentsInfo(
  val filescount: Int,
  val filessize: Int,
  val lastmodified: Long,
  val mimetypes: List<String>,
  val repositorytype: String,
)
