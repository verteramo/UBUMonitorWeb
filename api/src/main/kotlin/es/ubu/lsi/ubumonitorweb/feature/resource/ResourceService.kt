package es.ubu.lsi.ubumonitorweb.feature.resource

import es.ubu.lsi.ubumonitorweb.core.moodle.Credentials
import es.ubu.lsi.ubumonitorweb.core.moodle.ResourceClient
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

@Service
@ImportHttpServices(
  ResourceClient::class,
  CoreEnrolClient::class,
)
class UserService(
  private val resourceClient: ResourceClient,
) {
  private val credentials: Credentials?
    get() = SecurityContextHolder.getContext().authentication?.credentials as? Credentials

  fun getUserIcon(
    id: Int,
    size: String,
  ) = credentials?.run { resourceClient.getUserIcon(id, size, token) }
}
