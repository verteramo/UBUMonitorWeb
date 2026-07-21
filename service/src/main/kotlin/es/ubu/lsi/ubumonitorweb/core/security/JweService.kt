package es.ubu.lsi.ubumonitorweb.core.security

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KClass

/**
 * Servicio de generación de tokens JWT.
 *
 * Al ser una aplicación de escritorio que se ejecuta localmente, se autogenera
 * la clave en memoria en cada arranque, garantizando que ningún secreto
 * criptográfico se quede guardado en el disco del usuario, anulando ataques de
 * ingeniería inversa sobre el archivo war o la base de datos local.
 */
@Service
class JwtService(private val mapper: ObjectMapper) {

  companion object {
    private const val BYTES_LENGTH = 32
    private const val CRYPT_ALGORITHM = "AES"
    private const val DIGEST_ALGORITHM = "SHA-256"
    private const val EXPIRATION: Long = 60 * 60 * 24
  }

  private val logger = KotlinLogging.logger {}

  private val key: SecretKeySpec

  init {
    val randomBytes = ByteArray(BYTES_LENGTH)
    SecureRandom().nextBytes(randomBytes)

    key = SecretKeySpec(
      MessageDigest.getInstance(DIGEST_ALGORITHM).digest(randomBytes), CRYPT_ALGORITHM,
    )

    val bytes = key.encoded
    val hex = bytes.joinToString("") { "%02x".format(it) }
    val base64 = Base64.getEncoder().encodeToString(bytes)

    logger.debug { "Private key (hex): $hex" }
    logger.debug { "Private key (base64): $base64" }
  }

  fun generateToken(payload: Any): String {
    return Jwts
        .builder()
        .issuedAt(Date())
        .expiration(Date.from(Instant.now().plusSeconds(EXPIRATION)))
        .claim(payload::class.simpleName, payload)
        .encryptWith(key, Jwts.ENC.A256GCM)
        .compact()
  }

  fun <T : Any> extract(jwe: String, type: KClass<T>): T {
    return mapper.convertValue(
      Jwts.parser().decryptWith(key).build().parseEncryptedClaims(jwe).payload[type.simpleName],
      type.java,
    )
  }

  final inline fun <reified T : Any> extract(jwe: String): T {
    return extract(jwe, T::class)
  }
}
