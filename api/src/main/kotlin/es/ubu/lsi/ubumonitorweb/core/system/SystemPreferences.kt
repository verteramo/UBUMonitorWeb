package es.ubu.lsi.ubumonitorweb.core.system

/**
 * Preferencias estáticas y globales del sistema (persistidas en YAML).
 */
data class SystemPreferences(
  val theme: String = "system",
  val sites: List<String> = emptyList(),
  val usernames: List<String> = emptyList(),
  val rememberSite: Boolean = false,
  val rememberUsername: Boolean = false,
)
