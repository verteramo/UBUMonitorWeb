/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

/**
 * Representa a un participante dentro del ámbito de un curso en Moodle.
 *
 * @property id Identificador único en Moodle.
 * @property username Nombre de usuario.
 * @property email Correo electrónico.
 * @property fullName Nombre completo.
 * @property picture URL de la imagen de perfil, si está disponible.
 * @property firstAccessTs Marca temporal del primer acceso al sitio.
 * @property lastAccessTs Marca temporal del último acceso al sitio.
 * @property lastCourseAccessTs Marca temporal del último acceso al curso específico.
 * @property country País.
 * @property phones Conjunto de números de teléfono asociados.
 * @property groups Lista de nombres de los grupos a los que pertenece.
 * @property roles Lista de roles asignados en el contexto del curso.
 * @property courses Lista de cursos en los que está matriculado.
 */
data class Participant(
  val id: Int,
  val username: String?,
  val email: String?,
  val fullName: String,
  val picture: String?,
  val firstAccessTs: Long?,
  val lastAccessTs: Long?,
  val lastCourseAccessTs: Long?,
  val country: String?,
  val phones: Set<String>,
  val groups: List<String>,
  val roles: List<String>,
  val courses: List<String>,
)
