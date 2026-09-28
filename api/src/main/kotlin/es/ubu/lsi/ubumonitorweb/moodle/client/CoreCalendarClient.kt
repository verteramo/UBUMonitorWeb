/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpMultiValueMap
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCalendarEvent

@Client("webservice-client")
interface CoreCalendarClient {
  data class CalendarEventsResponse(
    val events: List<MoodleCalendarEvent>,
  )

  @Client
  fun getCalendarEvents(
    @PhpMultiValueMap events: Map<String, List<Int>>,
  ): CalendarEventsResponse
}
