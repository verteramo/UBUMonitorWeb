package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security")
data class SecurityProperties(
  val publicRoutes: Set<String> = emptySet(),
)
