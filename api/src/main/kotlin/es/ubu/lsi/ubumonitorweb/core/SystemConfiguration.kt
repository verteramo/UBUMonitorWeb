package es.ubu.lsi.ubumonitorweb.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.dataformat.csv.CsvMapper
import tools.jackson.module.kotlin.kotlinModule

/**
 * Configuraciones globales del sistema.
 */
@Configuration
class SystemConfiguration {
  /**
   * CsvMapper global para toda la aplicación.
   */
  @Bean
  fun csvMapper(): CsvMapper = CsvMapper.builder().addModule(kotlinModule()).build()
}
