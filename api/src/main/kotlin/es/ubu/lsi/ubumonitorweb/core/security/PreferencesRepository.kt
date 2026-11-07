package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService.Companion.asString
import es.ubu.lsi.ubumonitorweb.core.system.SystemProperties
import es.ubu.lsi.ubumonitorweb.domain.Preferences
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.readValue
import java.io.File

/**
 * Repositorio para la persistencia local de las preferencias del usuario en formato JSON.
 *
 * Los archivos se nombran utilizando el hash de la combinación de la URL del sitio
 * y el ID del usuario para garantizar unicidad y seguridad en el sistema de archivos.
 * El contenido se guarda en texto plano para facilitar la hidratación directa en el frontend.
 *
 * @property properties Propiedades del sistema donde se configuran los directorios.
 * @property digestService Servicio de derivación para generar el nombre del archivo.
 * @property mapper Instancia de Jackson para la serialización.
 */
@Repository
class PreferencesRepository(
  private val mapper: YAMLMapper,
  private val properties: SystemProperties,
  private val digestService: DigestService,
) {
  private val logger = KotlinLogging.logger {}

  init {
    properties.preferencesDirectory.mkdirs().also {
      logger.info { "Preferences directory: ${properties.preferencesDirectory.absolutePath}" }
    }
  }

  /**
   * Resuelve el archivo correspondiente combinando el host y el identificador del usuario.
   *
   * @param siteUrl URL del sitio Moodle.
   * @param userId Identificador único del usuario.
   * @return Descriptor [File] del archivo de destino.
   */
  private fun getFile(
    siteUrl: String,
    userId: Int,
  ): File {
    val identifier = "$siteUrl:$userId"
    val digest = digestService.digest(identifier)
    return properties.preferencesDirectory.resolve("${digest.asString()}.yaml")
  }

  /**
   * Recupera y deserializa las preferencias almacenadas vinculadas al usuario.
   *
   * @param siteUrl URL del sitio Moodle.
   * @param userId Identificador único del usuario.
   * @return Instancia de [Preferences] recuperada, o una instancia por defecto si el archivo no existe o la lectura falla.
   */
  fun readPreferences(
    siteUrl: String,
    userId: Int,
  ): Preferences =
    getFile(siteUrl, userId).takeIf { it.exists() }?.let { file ->
      runCatching {
        logger.debug { "Reading preferences from: ${file.path}" }
        mapper.readValue<Preferences>(file)
      }.onFailure {
        logger.error(it) { "Error reading preferences, returning defaults" }
      }.getOrNull()
    } ?: Preferences()

  /**
   * Serializa y escribe en disco la información de las preferencias.
   *
   * @param preferences Preferencias a persistir (estado completo del usuario).
   * @param siteUrl URL del sitio Moodle.
   * @param userId Identificador único del usuario.
   */
  fun writePreferences(
    preferences: Preferences,
    siteUrl: String,
    userId: Int,
  ) {
    getFile(siteUrl, userId).let { file ->
      runCatching {
        logger.debug { "Writing preferences to: ${file.path}" }
        mapper.writeValue(file, preferences)
      }.onFailure {
        logger.error(it) { "Error writing preferences" }
      }
    }
  }
}
