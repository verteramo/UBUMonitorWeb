/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleLogEntry
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientProfile
interface ReportLogClient {
  @GetExchange
  fun getLogs(
    @RequestParam id: Int,
    @RequestParam date: Long? = null,
    @RequestParam origin: String? = null,
  ): List<List<MoodleLogEntry>>
}
