package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.crypto.CipherStrategy
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService
import es.ubu.lsi.ubumonitorweb.core.crypto.DigestService.Companion.asString
import es.ubu.lsi.ubumonitorweb.domain.User
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleSiteInfo
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleUser
import org.springframework.stereotype.Service
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.readValue

@Service
class UserService(
  private val mapper: YAMLMapper,
  private val digestService: DigestService,
  private val cipherStrategy: CipherStrategy,
  private val userRepository: UserRepository,
) {
  private fun getId(
    email: String?,
    siteUrl: String,
    userId: Int,
  ): String = email?.trim()?.lowercase()?.takeIf { it.isNotBlank() } ?: (siteUrl + userId.toString())

  private fun getIdHash(
    email: String?,
    siteUrl: String,
    userId: Int,
  ): String = digestService.digest(getId(email, siteUrl, userId)).asString()

  fun findOrCreate(
    email: String?,
    siteUrl: String,
    userId: Int,
  ): UserEntity =
    getIdHash(email, siteUrl, userId).let { hash ->
      userRepository.findById(hash).orElseGet { UserEntity().apply { id = hash } }
    }

  fun findOrCreate(
    moodleUser: MoodleUser?,
    moodleSiteInfo: MoodleSiteInfo,
  ): UserEntity = findOrCreate(moodleUser?.email, moodleSiteInfo.siteurl, moodleSiteInfo.userid)

  fun findOrCreate(user: User): UserEntity = findOrCreate(user.email, user.siteUrl, user.id)

  fun getVault(
    userEntity: UserEntity,
    token: String,
  ): UserEntity.Vault? =
    runCatching {
      userEntity.vault?.let {
        cipherStrategy.decrypt(it, digestService.digest(token)).let { bytes ->
          mapper.readValue<UserEntity.Vault>(bytes)
        }
      }
    }.getOrNull()

  fun setVault(
    userEntity: UserEntity,
    newVault: UserEntity.Vault,
    token: String,
  ) {
    runCatching {
      cipherStrategy.encrypt(mapper.writeValueAsBytes(newVault), digestService.digest(token)).let {
        userEntity.vault = it
      }
    }.onFailure {
      it.printStackTrace()
    }.getOrNull()
  }

  fun getPreferences(userEntity: UserEntity): MutableMap<String, Any> =
    userEntity.preferences?.let { mapper.readValue<MutableMap<String, Any>>(it) } ?: mutableMapOf()

  fun setPreferences(
    userEntity: UserEntity,
    preferences: Map<String, Any>,
  ) {
    userEntity.preferences = mapper.writeValueAsString(preferences)
  }

  fun save(userEntity: UserEntity) {
    userRepository.save(userEntity)
  }
}
