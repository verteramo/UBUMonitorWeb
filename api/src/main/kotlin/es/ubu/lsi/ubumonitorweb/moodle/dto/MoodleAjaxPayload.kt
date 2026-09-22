package es.ubu.lsi.ubumonitorweb.moodle.dto

data class MoodleAjaxPayload(
  val index: Int = 0,
  val methodname: String,
  val args: Map<String, Any> = emptyMap(),
)
