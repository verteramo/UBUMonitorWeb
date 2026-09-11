/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.resource.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

@ClientProfile
interface ResourceClient {
  @GetExchange("/{id}/user/icon/{size}")
  fun getUserIcon(
    @PathVariable id: Int,
    @PathVariable size: String,
  ): ByteArray
}
