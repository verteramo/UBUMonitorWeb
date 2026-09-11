package es.ubu.lsi.ubumonitorweb.domain

data class LogEntry(
  val time: String,
  val userFullName: String,
  val affectedUser: String,
  val context: String,
  val component: String,
  val event: String,
  val description: String,
  val origin: String,
  val ipAddress: String,
)
