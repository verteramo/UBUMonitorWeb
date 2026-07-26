package es.ubu.lsi.ubumonitorweb.core.security

import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.DirectDecrypter
import com.nimbusds.jose.crypto.DirectEncrypter
import es.ubu.lsi.ubumonitorweb.feature.service.MoodleToken
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import javax.crypto.KeyGenerator

/**
 * Servicio codificador/decodificador de tokens del tipo [MoodleToken] con envoltura JWE.
 *
 * Al ser una aplicación de escritorio que se ejecuta localmente, se autogenera la clave en memoria
 * en cada arranque, garantizando que ningún secreto criptográfico almacenado previamente sea
 * válido.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Service
@ConditionalOnProperty("security.codec", havingValue = "jwe")
class JWECodec(
    private val mapper: ObjectMapper,
    private val properties: Properties,
) : AuthCodec<MoodleToken> {

  /**
   * Propiedades de configuración.
   *
   * @param algorithm Algoritmo de encriptación.
   * @param keySize Tamaño de la clave en bits.
   */
  @ConfigurationProperties("security.codec.jwe")
  data class Properties(val algorithm: String, val keySize: Int)

  /** Cifrador */
  private val encrypter: DirectEncrypter

  /** Descifrador */
  private val decrypter: DirectDecrypter

  init {
    /**
     * Construcción de la clave simétrica para el cifrador y descifrador.
     */
    KeyGenerator.getInstance(
      properties.algorithm,
    ).apply {
      init(
        properties.keySize,
      )
    }.generateKey().let {
      encrypter = DirectEncrypter(it)
      decrypter = DirectDecrypter(it)
    }
  }

  /**
   * Codifica token a cadena.
   *
   * @param payload Token.
   * @return Cadena.
   */
  override fun encode(payload: MoodleToken): String {
    val data = mapper.writeValueAsString(payload)
    val jweHeader = JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
    val jweObject = JWEObject(jweHeader, Payload(data))
    jweObject.encrypt(encrypter)
    return jweObject.serialize()
  }

  /**
   * Decodifica cadena a token.
   *
   * @param data Cadena.
   * @return Token.
   */
  override fun decode(data: String): MoodleToken {
    val jweObject = JWEObject.parse(data)
    jweObject.decrypt(decrypter)
    return mapper.readValue<MoodleToken>(jweObject.payload.toString())
  }
}
