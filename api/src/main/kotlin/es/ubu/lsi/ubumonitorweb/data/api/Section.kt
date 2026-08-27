/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.api

data class Section(
  val id: Int,
  val name: String?,
  val visible: Boolean,
  val userVisible: Boolean,
  val order: Int?,
  val modules: List<Module>,
)
