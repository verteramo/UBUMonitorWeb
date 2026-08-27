/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.resource

import es.ubu.lsi.ubumonitorweb.core.moodle.Credentials
import es.ubu.lsi.ubumonitorweb.feature.resource.client.ResourceClient
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

@Service
@ImportHttpServices(
  ResourceClient::class,
)
class ResourceService(
  private val resourceClient: ResourceClient,
) {
  private val credentials: Credentials?
    get() = SecurityContextHolder.getContext().authentication?.credentials as? Credentials

  fun getUserIcon(
    id: Int,
    size: String,
  ) = credentials?.run { resourceClient.getUserIcon(id, size, token) }
}
