package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpMap
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleCalendarEvent
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
