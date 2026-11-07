package es.ubu.lsi.ubumonitorweb.domain

data class Preferences(
  val theme: String = "light",
  val language: String = "en",
  val courses: Map<Int, Any> = emptyMap(),
)
