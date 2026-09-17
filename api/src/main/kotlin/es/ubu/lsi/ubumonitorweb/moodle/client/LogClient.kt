/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientProfile
interface LogClient {
  @GetExchange
  fun getCsvLogs(
    @RequestParam id: Int,
    @RequestParam date: Long? = null,
    @RequestParam origin: String? = null,
  ): String
}
