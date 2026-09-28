/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.system.ApplicationHome
import java.io.File

/**
 * Propiedades de configuración de seguridad.
 *
 * @property publicRoutes Conjunto de rutas públicas.
 * @property digestAlgorithm Algoritmo de derivación.
 * @property cookiesDirectory Directorio donde se almacenan las cookies (por defecto al nivel del ejecutable).
 */
@ConfigurationProperties("security")
data class SecurityProperties(
  val publicRoutes: Set<String> = emptySet(),
  val digestAlgorithm: String = "SHA-256",
  val cookiesDirectory: File = ApplicationHome().dir,
)
