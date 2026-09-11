package es.ubu.lsi.ubumonitorweb.data.dto

import es.ubu.lsi.ubumonitorweb.data.api.LogEntry

data class MoodleLogEntry(
  val time: String,
  val userfullname: String,
  val affecteduser: String,
  val eventcontext: String,
  val component: String,
  val eventname: String,
  val description: String,
  val origin: String,
  val ipaddress: String,
) {
  fun toLogEntry() =
    LogEntry(
      time = time,
      userFullName = userfullname,
      affectedUser = affecteduser,
      context = eventcontext,
      component = component,
      event = eventname,
      description = description,
      origin = origin,
      ipAddress = ipaddress,
    )
}
