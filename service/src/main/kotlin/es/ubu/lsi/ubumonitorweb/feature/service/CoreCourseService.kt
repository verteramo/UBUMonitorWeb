package es.ubu.lsi.ubumonitorweb.feature.service

import es.ubu.lsi.ubumonitorweb.core.service.ServiceProfile
import es.ubu.lsi.ubumonitorweb.feature.dto.MoodleCategory
import es.ubu.lsi.ubumonitorweb.feature.dto.MoodleClassifiedCourses
import es.ubu.lsi.ubumonitorweb.feature.dto.MoodleCourse
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ServiceProfile("webservice")
interface CoreCourseService {
  @PostExchange
  fun getRecentCourses(): List<MoodleCourse>

  @PostExchange
  fun getEnrolledCoursesByTimelineClassification(
    @RequestParam classification: String,
  ): MoodleClassifiedCourses

  @PostExchange
  fun getCategories(
    @RequestParam("criteria[0][key]") key: String,
    @RequestParam("criteria[0][value]") value: String,
  ): List<MoodleCategory>
}
