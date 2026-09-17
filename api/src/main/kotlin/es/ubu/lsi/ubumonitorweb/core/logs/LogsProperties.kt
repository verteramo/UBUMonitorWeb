package es.ubu.lsi.ubumonitorweb.core.logs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("logs")
data class LogsProperties(
  val templatesFilename: String = "logs-templates.yaml",
  val booleanPrefixes: List<String> = emptyList(),
)
