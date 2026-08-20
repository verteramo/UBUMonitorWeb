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
      fullname = fullname,
      username = username,
      firstname = firstname,
      lastname = lastname,
      initials = initials,
      email = email,
      address = address,
      phone1 = phone1,
      phone2 = phone2,
      department = department,
      institution = institution,
      idnumber = idnumber,
      interests = interests,
      firstaccess = firstaccess,
      lastaccess = lastaccess,
      lastcourseaccess = lastcourseaccess,
      description = description,
      descriptionformat = descriptionformat,
      city = city,
      country = country,
      profileimageurl = profileimageurl,
      // Transformaciones de colecciones (asumiendo los nombres de las propiedades internas)
      customfields = customfields?.associate { it.shortname to it.value } ?: emptyMap(),
      groups = groups?.map { it.name } ?: emptyList(),
      roles = roles?.map { it.shortname } ?: emptyList(),
      preferences = preferences?.associate { it.name to it.value } ?: emptyMap(),
      enrolledcourses = enrolledcourses?.map { it.fullname } ?: emptyList(),
    )

  fun getUsersByCourseId(id: Int): List<User> = coreEnrolClient.getEnrolledUsers(id).map { it.toUser() }

  fun getUserIcon(
    id: Int,
    size: String,
  ) = credentials?.run { resourceClient.getUserIcon(id, size, token) }
}
