package es.ubu.lsi.ubumonitorweb.core.moodle

/**
 * Objeto de credenciales de Moodle.
 *
 * @author Marcelo Verteramo Pérsico
 */
data class Credentials(
  val token: String,
  val privatetoken: String,
)
