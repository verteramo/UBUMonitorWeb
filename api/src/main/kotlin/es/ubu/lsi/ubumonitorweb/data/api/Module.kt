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
  val type: String,
  val plural: String,
  val purpose: String,
  val picture: String,
  val completion: Int,
  val isVisible: Boolean,
  val isUserVisible: Boolean,
  val sinceTs: Long?,
  val untilTs: Long?,
)
