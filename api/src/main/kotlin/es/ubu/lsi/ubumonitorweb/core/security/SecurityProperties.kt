package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Propiedades de configuración de seguridad.
 *
 * @param permitAll Conjunto de rutas públicas.
 *
 * @author Marcelo Verteramo Pérsico
 */
@ConfigurationProperties("security")
data class SecurityProperties(
  val permitAll: Set<String> = emptySet(),
)
