package es.ubu.lsi.ubumonitorweb.data.api

data class Completion(
  val cmid: Int,
  val module: String,
  val instance: Int,
  val state: Int?,
  val completedMs: Long?,
  val tracking: Int,
  val overrideBy: Int?,
  val isCompletionEnabled: Boolean,
  val isValueUsed: Boolean,
  val isAutomatic: Boolean,
  val isTrackedUser: Boolean,
  val isUserVisible: Boolean,
  val isOverallComplete: Boolean,
)
