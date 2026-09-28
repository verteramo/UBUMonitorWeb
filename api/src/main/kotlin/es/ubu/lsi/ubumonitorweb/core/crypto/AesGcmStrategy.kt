/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.crypto

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Implementación especializa de [CipherStrategy] utilizando el algoritmo AES en modo GCM.
 *
 * Se habilita condicionalmente mediante la propiedad `security.cipher-strategy` establecida en `aes-gcm`.
 */
@Component
@ConditionalOnProperty(
  name = ["security.cipher-strategy"],
  havingValue = "aes-gcm",
  matchIfMissing = true,
)
class AesGcmStrategy : CipherStrategy {
  /**
   * Tamaño del vector de inicialización.
   */
  private val ivSize = 12

  /**
   * Longitud en bits del tag de autenticación.
   */
  private val tagLength = 128

  /**
   * Algoritmo de cifrado.
   */
  private val algorithm = "AES"

  /**
   * Parámetros de transformación.
   */
  private val transformation = "AES/GCM/NoPadding"

  /**
   * Cifra datos utilizando AES-GCM, anteponiendo un vector de inicialización aleatorio.
   *
   * @param data Datos.
   * @param key Clave simétrica.
   * @return Datos cifrados.
   */
  override fun encrypt(
    data: ByteArray,
    key: ByteArray,
  ): ByteArray {
    val secretKey = SecretKeySpec(key, algorithm)
    val iv = ByteArray(ivSize).apply { SecureRandom().nextBytes(this) }
    val cipher =
      Cipher.getInstance(transformation).apply { init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(tagLength, iv)) }

    return iv + cipher.doFinal(data)
  }

  /**
   * Descifra un paquete de datos binarios estructurado con el IV al inicio.
   *
   * @param encryptedData Datos cifrados.
   * @param key Clave simétrica.
   * @return Datos descifrados.
   */
  override fun decrypt(
    encryptedData: ByteArray,
    key: ByteArray,
  ): ByteArray {
    val secretKey = SecretKeySpec(key, algorithm)
    val iv = encryptedData.copyOfRange(0, ivSize)
    val cipherText = encryptedData.copyOfRange(ivSize, encryptedData.size)
    val cipher =
      Cipher.getInstance(transformation).apply { init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(tagLength, iv)) }

    return cipher.doFinal(cipherText)
  }
}
