package es.ubu.lsi.ubumonitorweb.feature.service

import es.ubu.lsi.ubumonitorweb.core.service.ServiceProfile
import es.ubu.lsi.ubumonitorweb.feature.dto.MoodleCourse
import es.ubu.lsi.ubumonitorweb.feature.dto.MoodleUserCourse
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ServiceProfile("webservice")
interface CoreEnrolService {
  @PostExchange
  fun getUsersCourses(
    @RequestParam userid: Int,
    @RequestParam returnusercount: Int = 0,
  ): List<MoodleUserCourse>
}
