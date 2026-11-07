package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity {
  @Id
  @Column(nullable = false, length = 64)
  var id: String? = null

  @Column(nullable = false)
  var theme: String = "system"
}
