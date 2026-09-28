package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.siteTimezone
import es.ubu.lsi.ubumonitorweb.core.system.ScrapingExtensions.toZoneId
import es.ubu.lsi.ubumonitorweb.domain.User
import es.ubu.lsi.ubumonitorweb.moodle.client.CoreUserClient
import es.ubu.lsi.ubumonitorweb.moodle.client.LoginClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleSiteInfo
import org.springframework.stereotype.Component
import org.springframework.web.service.registry.ImportHttpServices
import java.time.ZoneId

@Component
@ImportHttpServices(
  LoginClient::class,
  CoreUserClient::class,
)
class UserAssembler(
  private val loginClient: LoginClient,
  private val coreUserClient: CoreUserClient,
) {
  fun MoodleSiteInfo.toUser(
    timezone: ZoneId,
    siteTimezone: ZoneId,
  ) = User(
    id = userid,
    username = username,
    isAdmin = userissiteadmin == true,
    language = lang,
    firstName = firstname,
    lastName = lastname,
    fullName = fullname,
    picture = userpictureurl,
    timezone = timezone,
    siteUrl = siteurl,
    siteName = sitename,
    siteVersion = version,
    siteRelease = release,
    siteTimezone = siteTimezone,
  )

  fun assemble(
    token: String,
    resolveMoodleSiteInfo: () -> MoodleSiteInfo,
  ): User {
    val moodleSiteInfo = resolveMoodleSiteInfo()
    val siteTimezone = loginClient.getUserEditForm().siteTimezone.toZoneId("UTC")

    val userTimezone =
      coreUserClient
        .getUsersByField(token, "id", listOf(moodleSiteInfo.userid))
        .firstOrNull()
        ?.timezone
        .toZoneId(
          siteTimezone.id,
        )

    return moodleSiteInfo.toUser(userTimezone, siteTimezone)
  }
}
