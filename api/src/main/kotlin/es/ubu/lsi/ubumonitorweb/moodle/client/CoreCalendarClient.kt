/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpMap
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCalendarEvent
import org.springframework.web.service.annotation.PostExchange

@ClientProfile("webservice-client")
interface CoreCalendarClient {
  data class CalendarEventsResponse(
    val events: List<MoodleCalendarEvent>,
  )

  @PostExchange
  fun getCalendarEvents(
    @PhpMap events: Map<String, List<Int>>,
  ): CalendarEventsResponse
}
