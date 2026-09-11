package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.domain.Event

data class MoodleCalendarEvent(
  val id: Int,
  val name: String,
  val description: String?,
  val format: Int,
  val courseid: Int,
  val categoryid: Int?,
  val groupid: Int?,
  val userid: Int?,
  val repeatid: Int?,
  val modulename: String?,
  val instance: Int?,
  val eventtype: String,
  val timestart: Long,
  val timeduration: Long,
  val visible: Int,
  val uuid: String?,
  val sequence: Int,
  val timemodified: Long,
  val subscriptionid: Int?,
) {
  fun toEvent() =
    Event(
      id = id,
      name = name,
      description = description,
      format = format,
      courseId = courseid,
      categoryId = categoryid,
      groupId = groupid,
      userId = userid,
      repeatId = repeatid,
      module = modulename,
      instance = instance,
      type = eventtype,
      startTs = timestart,
      durationTs = timeduration,
      isVisible = visible == 1,
      uuid = uuid,
      sequence = sequence,
      modifiedTs = timemodified,
      subscriptionId = subscriptionid,
    )
}
