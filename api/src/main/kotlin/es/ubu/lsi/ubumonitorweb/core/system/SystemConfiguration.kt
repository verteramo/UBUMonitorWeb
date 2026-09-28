/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.system

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.dataformat.csv.CsvMapper
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.kotlinModule

/**
 * Configuración global de componentes y beans transversales del sistema.
 */
@Configuration
class SystemConfiguration {
  /**
   * Proporciona la instancia compartida de [CsvMapper] configurada con soporte para tipos de Kotlin.
   *
   * @return Mapper CSV.
   */
  @Bean
  fun csvMapper(): CsvMapper = CsvMapper.builder().addModule(kotlinModule()).build()

  /**
   * Proporciona la instancia compartida de [YAMLMapper] con detección automática de módulos en el classpath.
   *
   * @return Mapper YAML.
   */
  @Bean
  fun yamlMapper(): YAMLMapper = YAMLMapper.builder().findAndAddModules().build()
}
