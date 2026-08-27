/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.api

data class Module(
  val id: Int,
  val url: String?,
  val name: String,
  val visible: Boolean,
  val userVisible: Boolean,
  val type: String,
  val picture: String,
  val purpose: String,
  val plural: String,
  val completion: Int,
  val since: Long,
  val until: Long,
)
