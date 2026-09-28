package es.ubu.lsi.ubumonitorweb.moodle.dto

data class MoodleAjaxResponse<T>(
  val error: Boolean,
  val data: T,
)
