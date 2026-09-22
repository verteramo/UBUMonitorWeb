/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

@Client("resource-client")
interface ResourceClient {
  @GetExchange("/{id}/user/icon/{size}")
  fun getUserIcon(
    @PathVariable id: Int,
    @PathVariable size: String,
  ): ByteArray
}
