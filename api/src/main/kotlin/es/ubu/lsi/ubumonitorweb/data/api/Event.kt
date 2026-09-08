package es.ubu.lsi.ubumonitorweb.data.api

data class Event(
  val id: Int,
  val name: String,
  val description: String?,
  val format: Int,
  val courseId: Int,
  val categoryId: Int?,
  val groupId: Int?,
  val userId: Int?,
  val repeatId: Int?,
  val module: String?,
  val instance: Int?,
  val type: String,
  val startMs: Long,
  val durationMs: Long,
  val isVisible: Boolean,
  val uuid: String?,
  val sequence: Int,
  val modifiedMs: Long,
  val subscriptionId: Int?,
)
