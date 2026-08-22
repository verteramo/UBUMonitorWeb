package es.ubu.lsi.ubumonitorweb.feature.user

import es.ubu.lsi.ubumonitorweb.core.moodle.Credentials
import es.ubu.lsi.ubumonitorweb.core.moodle.ResourceClient
import es.ubu.lsi.ubumonitorweb.feature.user.api.User
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

  private fun MoodleUser.toUser() =
    User(
      id = id,
      username = username,
      email = email,
      fullName = fullname,
      picture = profileimageurl,
      firstAccess = firstaccess,
      lastAccess = lastaccess,
      lastCourseAccess = lastcourseaccess,
      country = country,
      // Transformaciones de colecciones
      phones = setOfNotNull(phone1, phone2),
      groups = groups?.map { it.name } ?: emptyList(),
      roles = roles?.map { it.shortname } ?: emptyList(),
      courses = enrolledcourses?.map { it.fullname } ?: emptyList(),
    )

  fun getUsersByCourseId(id: Int): List<User> = coreEnrolClient.getEnrolledUsers(id).map { it.toUser() }

  fun getUserIcon(
    id: Int,
    size: String,
  ) = credentials?.run { resourceClient.getUserIcon(id, size, token) }
}
