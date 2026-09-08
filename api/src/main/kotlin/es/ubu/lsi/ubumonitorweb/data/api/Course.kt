/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.api

data class Course(
  val id: Int,
  val name: String,
  val picture: String?,
  val starred: Boolean,
  val sinceMs: Long?,
  val untilMs: Long?,
  val category: String,
)
