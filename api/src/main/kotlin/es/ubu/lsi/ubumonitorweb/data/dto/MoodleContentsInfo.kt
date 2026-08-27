/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

data class MoodleContentsInfo(
  val filescount: Int,
  val filessize: Int,
  val lastmodified: Long,
  val mimetypes: List<String>,
  val repositorytype: String?,
)
