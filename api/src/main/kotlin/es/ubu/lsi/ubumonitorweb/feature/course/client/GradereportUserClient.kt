package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.data.api.UserGrade
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleUserGrade
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleWarning
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@ClientProfile("webservice-client")
interface GradereportUserClient {
  data class GradeItemsResponse(
    val usergrades: List<MoodleUserGrade>,
    val warnings: List<MoodleWarning>? = emptyList(),
  ) {
    fun toUserGrades(): List<UserGrade> = usergrades.map { it.toUserGrade() }
  }

  @PostExchange
  fun getGradeItems(
    @RequestParam courseid: Int,
  ): GradeItemsResponse
}
