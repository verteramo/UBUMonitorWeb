package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleContent(
  val type: String,
  val filename: String,
  val filepath: String,
  val filesize: Int,
  val fileurl: String? = null,
  val content: String? = null,
  val timecreated: Long? = null,
  val timemodified: Long,
  val sortorder: Int,
  val userid: Int? = null,
  val author: String? = null,
  val license: String? = null,
  val tags: List<String>? = null,
  val mimetype: String? = null,
  val isexternalfile: Boolean? = null,
)
