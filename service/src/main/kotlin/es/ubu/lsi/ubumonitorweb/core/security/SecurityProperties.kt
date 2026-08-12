package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Propiedades de configuración de seguridad.
 *
 * @param publicRoutes Conjunto de rutas públicas.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@ConfigurationProperties("security")
data class SecurityProperties(
  val publicRoutes: Set<String> = emptySet(),
)
