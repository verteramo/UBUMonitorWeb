package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "cookies")
class CookieEntity {
  @Id
  @Column(nullable = false, length = 64)
  var id: String? = null

  @Column(nullable = false, columnDefinition = "BLOB")
  var data: ByteArray? = null
}
