package es.ubu.lsi.ubumonitorweb.core.security

/**
 * Objeto con los datos del principal que provee Moodle.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
data class MoodlePrincipal(
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
