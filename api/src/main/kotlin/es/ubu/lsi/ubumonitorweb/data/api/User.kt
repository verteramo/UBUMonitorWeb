/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.api

data class User(
  val id: Int,
  val username: String?,
  val email: String?,
  val fullName: String,
  val picture: String?,
  val firstAccess: Long?,
  val lastAccess: Long?,
  val lastCourseAccess: Long?,
  val country: String?,
  val phones: Set<String>,
  val groups: List<String>,
  val roles: List<String>,
  val courses: List<String>,
)
