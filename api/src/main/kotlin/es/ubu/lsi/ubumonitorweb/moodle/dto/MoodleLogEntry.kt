/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MoodleLogEntry(
  @JsonProperty("Time") val time: String,
  @JsonProperty("Component") val component: String,
  @JsonProperty("Event name") val eventname: String,
  @JsonProperty("Description") val description: String,
  @JsonProperty("Origin") val origin: String,
  @JsonProperty("IP address") val ipaddress: String,
)
