package es.ubu.lsi.ubumonitorweb.core.security

data class Principal(
  val id: Int,
  val username: String,
  val isAdmin: Boolean,
  val language: String,
  val firstName: String,
  val lastName: String,
  val fullName: String,
  val picture: String?,
  val platform: Platform,
)
