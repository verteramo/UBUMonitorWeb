package es.ubu.lsi.ubumonitorweb.core.system

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.readValue

@Service
class PreferencesService(
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
        mapper.writeValue(file, emptyMap<String, Any>())
        logger.info { "System preferences file created: '$file'." }
      }
    }
  }

  fun read(): Map<String, Any> =
    properties.preferencesFile.let { file ->
      runCatching {
        mapper.readValue<Map<String, Any>>(file).also {
          logger.debug { "System preferences read from: '$file'." }
        }
      }.onFailure {
        logger.error(it) { "Error reading system preferences from: '$file'." }
      }.getOrDefault(emptyMap())
    }

  fun write(preferences: Map<String, Any>) {
    properties.preferencesFile.let { file ->
      logger.debug { "Writing preferences: '$preferences'." }
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
