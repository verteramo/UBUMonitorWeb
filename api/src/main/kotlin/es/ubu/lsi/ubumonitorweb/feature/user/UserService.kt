package es.ubu.lsi.ubumonitorweb.feature.user

import es.ubu.lsi.ubumonitorweb.core.moodle.Credentials
import es.ubu.lsi.ubumonitorweb.core.moodle.ResourceClient
import es.ubu.lsi.ubumonitorweb.feature.user.client.CoreEnrolClient
import es.ubu.lsi.ubumonitorweb.feature.user.dto.MoodleUser
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
  private val coreEnrolClient: CoreEnrolClient,
) {
  private val credentials: Credentials?
    get() = SecurityContextHolder.getContext().authentication?.credentials as? Credentials

  fun getUsersByCourseId(id: Int): List<MoodleUser> = coreEnrolClient.getEnrolledUsers(id)

  fun getUserIcon(
    id: Int,
    size: String,
  ) = credentials?.run { resourceClient.getUserIcon(id, size, token) }
}
