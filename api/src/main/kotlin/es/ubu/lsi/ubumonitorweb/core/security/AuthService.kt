package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.SiteConfig
import es.ubu.lsi.ubumonitorweb.moodle.client.ToolMobileClient
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Servicio de autenticación que realiza todas las consultas necesarias a los distintos endpoints de Moodle para
 * recopilar toda la información necesaria para el contexto de Spring Security.
 */
@Service
@ImportHttpServices(
  ToolMobileClient::class,
)
class AuthService(
  private val toolMobileClient: ToolMobileClient,
) {
  fun discover(): SiteConfig? {
    val moodlePublicConfig = toolMobileClient.getPublicConfig()
    return moodlePublicConfig.firstOrNull()?.data?.toSiteConfig()
  }
}
