/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Propiedades de configuración de los logs.
 *
 * @property filename Nombre del fichero, tanto interno como externo, que contiene los templates.
 * @property booleanPrefixes Prefijos para identificar atributos booleanos (is, has, etc.).
 */
@ConfigurationProperties("logs")
data class LogsProperties(
  val filename: String = "logs-templates.yaml",
  val booleanPrefixes: List<String> = emptyList(),
)
