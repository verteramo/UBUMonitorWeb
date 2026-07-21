package es.ubu.lsi.ubumonitorweb.core.security

import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.DirectDecrypter
import com.nimbusds.jose.crypto.DirectEncrypter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.reflect.KClass

/**
 * Servicio de generación de tokens JWE.
 *
 * Al ser una aplicación de escritorio que se ejecuta localmente, se autogenera
 * la clave en memoria en cada arranque, garantizando que ningún secreto
 * criptográfico se quede guardado en el disco del usuario, anulando ataques de
 * ingeniería inversa sobre el archivo war o la base de datos local.
 */
@Service
class JweService(private val mapper: ObjectMapper) {

  companion object {
    private const val CRYPT_ALGORITHM = "AES"
  }

  private val logger = KotlinLogging.logger {}

  private val key: SecretKey = KeyGenerator.getInstance(
    CRYPT_ALGORITHM,
  ).apply {
    init(256)
  }.generateKey()

  fun generateToken(payload: Any): String {
    val jsonPayload = mapper.writeValueAsString(payload)
    val header = JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
    val jweObject = JWEObject(header, Payload(jsonPayload))

    jweObject.encrypt(DirectEncrypter(key))
    return jweObject.serialize()
  }

  fun <T : Any> extract(token: String, type: KClass<T>): T {
    val jweObject = JWEObject.parse(token)
    jweObject.decrypt(DirectDecrypter(key))
    return mapper.readValue(jweObject.payload.toString(), type.java)
  }

  final inline fun <reified T : Any> extract(token: String): T {
    return extract(token, T::class)
  }
}
