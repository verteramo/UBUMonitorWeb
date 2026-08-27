package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleContent(
  val type: String,
  val filename: String,
  val filepath: String?,
  val filesize: Int,
  val fileurl: String?,
  val content: String?,
  val timecreated: Long?,
  val timemodified: Long,
  val sortorder: Int?,
  val mimetype: String?,
  val isexternalfile: Boolean?,
  val repositorytype: String?,
  val userid: Int?,
  val author: String?,
  val license: String?,
  val tags: List<MoodleTag>?,
)
