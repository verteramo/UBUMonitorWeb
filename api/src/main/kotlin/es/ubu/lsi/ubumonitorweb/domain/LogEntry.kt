/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

import java.time.ZonedDateTime

data class LogEntry(
  val datetime: ZonedDateTime,
  val component: String,
  val event: String,
  val origin: String,
  val ipAddress: String,
  val attributes: Map<String, Any>,
)
