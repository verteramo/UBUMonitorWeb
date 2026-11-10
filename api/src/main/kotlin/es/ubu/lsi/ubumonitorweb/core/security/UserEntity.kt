package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity {
  data class Vault(
    val cookie: Cookie,
    val privateToken: String,
  ) {
    data class Cookie(
      val name: String,
      val value: String,
      val domain: String,
      val path: String,
      val isSecure: Boolean,
    )
  }

  @Id
  @Column(nullable = false, length = 64)
  var id: String? = null

  @Column(nullable = false, columnDefinition = "BLOB")
  var vault: ByteArray? = null

  @Column(columnDefinition = "TEXT")
  var preferences: String? = null
}
