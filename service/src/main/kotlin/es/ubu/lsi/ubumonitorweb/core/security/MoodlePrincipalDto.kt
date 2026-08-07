package es.ubu.lsi.ubumonitorweb.core.security

data class MoodlePrincipalDto(
  val username: String,
  val firstname: String,
  val lastname: String,
  val fullname: String,
  val lang: String,
  val userid: Int,
  val siteurl: String,
  val userpictureurl: String,
  val userissiteadmin: Boolean,
  val sitename: String,
)
