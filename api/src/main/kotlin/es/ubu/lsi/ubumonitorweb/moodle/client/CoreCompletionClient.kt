/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleActivityCompletion
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ClientProfile("webservice-client")
interface CoreCompletionClient {
  data class ActivitiesCompletionStatusResponse(
    val statuses: List<MoodleActivityCompletion>,
  )

  @PostExchange
  fun getActivitiesCompletionStatus(
    @RequestParam courseid: Int,
    @RequestParam userid: Int,
  ): ActivitiesCompletionStatusResponse
}
