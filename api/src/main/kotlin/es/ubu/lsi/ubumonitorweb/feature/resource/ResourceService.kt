/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.resource

import es.ubu.lsi.ubumonitorweb.moodle.client.ResourceClient
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

@Service
@ImportHttpServices(
  ResourceClient::class,
)
class ResourceService(
  private val resourceClient: ResourceClient,
) {
  fun getUserIcon(
    id: Int,
    size: String,
  ): ByteArray = resourceClient.getUserIcon(id, size)
}
