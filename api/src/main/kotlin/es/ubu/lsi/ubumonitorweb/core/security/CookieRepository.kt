package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.crypto.CipherStrategy
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService.Companion.asString
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.io.File

/**
 * Repositorio para la persistencia segura y cifrada de cookies en el sistema de archivos.
 *
 * Los archivos se indexan a partir del hash del token, mientras que su contenido se cifra
 * y deserializa mediante [CipherStrategy] y [ObjectMapper].
 *
 * @property properties Propiedades de configuración de seguridad.
 * @property digestService Servicio de derivación (hashing).
 * @property cipherStrategy Estrategia criptográfica para el cifrado y descifrado simétrico.
 * @property jsonMapper Instancia de Jackson para la serialización de las cookies.
 */
@Repository
class CookieRepository(
  private val properties: SecurityProperties,
  private val digestService: DigestService,
  private val cipherStrategy: CipherStrategy,
  private val jsonMapper: ObjectMapper,
) {
  /**
   * Representación inmutable y serializable de los atributos esenciales de una cookie HTTP para su almacenamiento local.
   *
   * @property name Nombre identificador.
   * @property value Valor textual.
   * @property domain Dominio/subdominio.
   * @property path Ruta del servidor.
   * @property isSecure Cookie solo por HTTPS.
   */
  data class Cookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val isSecure: Boolean,
  )

  private val logger = KotlinLogging.logger {}

  init {
    properties.cookiesDirectory.mkdirs().also {
      logger.info { "Cookies directory: ${properties.cookiesDirectory.absolutePath}" }
    }
  }

  /**
   * Resuelve el archivo correspondiente al hash del token suministrado.
   *
   * @param digest Hash binario del token utilizado como nombre del archivo.
   * @return Descriptor [File] del archivo de destino.
   */
  private fun getFile(digest: ByteArray): File = properties.cookiesDirectory.resolve(digest.asString())

  /**
   * Recupera y descifra la cookie almacenada vinculada al token indicado.
   *
   * @param token Token asociado a la cookie persistida.
   * @return Instancia de [Cookie] recuperada, o `null` si el archivo no existe o la lectura falla.
   */
  fun readCookie(token: String): Cookie? {
    val digest = digestService.digest(token)
    val file = getFile(digest)

    return file.takeIf { it.exists() }?.let { file ->
      runCatching {
        logger.debug { "Reading/decrypting cookie from: ${file.path}" }
        val bytes = file.readBytes()
        val decryptedBytes = cipherStrategy.decrypt(bytes, digest)
        jsonMapper.readValue<Cookie>(decryptedBytes)
      }.onFailure {
        logger.error(it) { "Error reading/decrypting cookie" }
      }.getOrNull()
    }
  }

  /**
   * Serializa, cifra y escribe en disco la información de una cookie vinculada a un token.
   *
   * @param cookie Cookie a persistir.
   * @param token Token para cifrar el archivo y determinar su nombre.
   */
  fun writeCookie(
    cookie: Cookie,
    token: String,
  ) {
    val digest = digestService.digest(token)
    val file = getFile(digest)

    runCatching {
      logger.debug { "Writing/encrypting cookie to: ${file.path}" }
      val bytes = jsonMapper.writeValueAsBytes(cookie)
      val encryptedBytes = cipherStrategy.encrypt(bytes, digest)
      file.writeBytes(encryptedBytes)
    }.onFailure {
      logger.error(it) { "Error writing/encrypting cookie" }
    }
  }
}
