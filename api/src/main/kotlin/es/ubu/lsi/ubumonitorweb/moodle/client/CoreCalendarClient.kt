/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpMap
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCalendarEvent
import org.springframework.web.service.annotation.PostExchange

@Client("webservice-client")
interface CoreCalendarClient {
  data class CalendarEventsResponse(
    val events: List<MoodleCalendarEvent>,
  )

  @PostExchange
  fun getCalendarEvents(
    @PhpMap events: Map<String, List<Int>>,
  ): CalendarEventsResponse
}
