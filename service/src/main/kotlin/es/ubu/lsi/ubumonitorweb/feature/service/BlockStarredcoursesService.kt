package es.ubu.lsi.ubumonitorweb.feature.service

import es.ubu.lsi.ubumonitorweb.core.service.ServiceProfile
import es.ubu.lsi.ubumonitorweb.feature.dto.MoodleCourse
import org.springframework.web.service.annotation.PostExchange

@ServiceProfile("webservice")
interface BlockStarredcoursesService {
  @PostExchange
  fun getStarredCourses(): List<MoodleCourse>
}
