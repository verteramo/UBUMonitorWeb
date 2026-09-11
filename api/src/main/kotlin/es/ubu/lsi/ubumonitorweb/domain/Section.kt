/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

data class Section(
  val id: Int,
  val name: String?,
  val order: Int?,
  val isVisible: Boolean,
  val isUserVisible: Boolean,
  val modules: List<Module>,
)
