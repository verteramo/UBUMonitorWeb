package es.ubu.lsi.ubumonitorweb.core.security

/**
 * Objeto de credenciales que provee Moodle.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
data class MoodleCredentials(
  val token: String,
  val privatetoken: String,
)
