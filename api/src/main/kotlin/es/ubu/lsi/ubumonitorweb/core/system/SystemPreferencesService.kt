package es.ubu.lsi.ubumonitorweb.core.system

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.readValue

@Service
class SystemPreferencesService(
  private val mapper: YAMLMapper,
  private val properties: SystemProperties,
) {
  private val logger = KotlinLogging.logger {}

  init {
    properties.preferencesFile.let { file ->
      file.parentFile.let { dir ->
        if (dir.mkdirs()) {
          logger.info { "Base directory created: '$dir'." }
        }
      }

      if (!file.exists()) {
        mapper.writeValue(file, SystemPreferences())
        logger.info { "System preferences file created: '$file'." }
      }
    }
  }

  fun read(): SystemPreferences =
    properties.preferencesFile.let { file ->
      runCatching {
        mapper.readValue<SystemPreferences>(file).also {
          logger.debug { "System preferences read from: '$file'." }
        }
      }.onFailure {
        logger.error(it) { "Error reading system preferences from: '$file'." }
      }.getOrDefault(SystemPreferences())
    }

  fun write(preferences: SystemPreferences) {
    properties.preferencesFile.let { file ->
      runCatching {
        mapper.writeValue(file, preferences).also {
          logger.debug { "System preferences written to: '$file'." }
        }
      }.onFailure {
        logger.error(it) { "Error writing system preferences to: '$file'." }
      }
    }
  }
}
