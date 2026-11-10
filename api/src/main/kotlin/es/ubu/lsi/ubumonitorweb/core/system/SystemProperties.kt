/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.system

import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.File

/**
 * Propiedades de configuración del sistema.
 *
 * @property publicRoutes Conjunto de rutas públicas.
 * @property digestAlgorithm Algoritmo de derivación.
 * @property baseDirectory Directorio base de la aplicación.
 */
@ConfigurationProperties("system")
data class SystemProperties(
  val publicRoutes: Set<String> = emptySet(),
  val digestAlgorithm: String = "SHA-256",
  val baseDirectory: File = File(System.getProperty("user.home"), ".umw"),
  val preferencesFile: File = baseDirectory.resolve("preferences.yaml"),
  val databaseFile: File = baseDirectory.resolve("database"),
)
