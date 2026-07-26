//package es.ubu.lsi.ubumonitorweb.feature.calendar
//
//import es.ubu.lsi.ubumonitorweb.core.base.MoodleRestService
//import es.ubu.lsi.ubumonitorweb.core.rest.resolver.UrlEncoded
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestHeader
//import org.springframework.web.bind.annotation.RequestParam
//import org.springframework.web.service.annotation.HttpExchange
//import org.springframework.web.service.annotation.PostExchange
//
//data class DayViewParams(
//  val year: Int,
//  val month: Int,
//  val day: Int,
//  val courseid: Int?,
//  val categoryid: Int?,
//)
//
//data class MyDate(val year: Int, val month: Int, val day: Int)
//
//@HttpExchange
//interface CoreCalendarService {
//
//  @PostExchange
//  fun getCalendarDayView(@UrlEncoded myDate: MyDate): Any
//}
