//package es.ubu.lsi.ubumonitorweb.feature.calendar
//
//import es.ubu.lsi.ubumonitorweb.core.mvc.handler.MandatoryHeaders
//import io.github.oshai.kotlinlogging.KotlinLogging
//import jakarta.annotation.Nullable
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import java.time.LocalDate
//
//data class OptionalParams(
//  @Nullable val courseid: Int?,
//
//  @Nullable val categoryid: Int?,
//)
//
//@RestController
//@RequestMapping("/calendar")
//@MandatoryHeaders("Moodle-Host")
//class CalendarController(private val calendarService: CoreCalendarService) {
//
//  private val logger = KotlinLogging.logger {}
//
//  @GetMapping("/{date}")
//  fun getCalendarDayView(
//    @PathVariable date: LocalDate,
//    optionalParams: OptionalParams?,
//  ): Any {
//
//    logger.debug { "OptionalParams: $optionalParams" }
//
//    return calendarService.getCalendarDayView(
//      MyDate(date.year, date.monthValue, date.dayOfMonth)
//    )
//  }
//}
