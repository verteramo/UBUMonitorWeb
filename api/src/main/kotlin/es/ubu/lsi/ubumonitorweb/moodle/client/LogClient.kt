/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@Client("log-client")
interface LogClient {
  @GetExchange
  fun getCsvLogs(
    @RequestParam id: Int,
    @RequestParam date: Long? = null,
    @RequestParam origin: String? = null,
  ): String
}
