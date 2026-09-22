/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Propiedades de configuración de seguridad.
 *
 * @param publicRoutes Conjunto de rutas públicas.
 */
@ConfigurationProperties("security")
data class SecurityProperties(
  val publicRoutes: Set<String> = emptySet(),
)
