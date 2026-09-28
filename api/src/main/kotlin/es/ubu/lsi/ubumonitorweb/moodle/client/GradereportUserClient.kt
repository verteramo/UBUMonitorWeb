/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleUserGrade
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@Client("webservice-client")
interface GradereportUserClient {
  data class GradeItemsResponse(
    val usergrades: List<MoodleUserGrade>,
  )

  @Client
  fun getGradeItems(
    @RequestParam courseid: Int,
  ): GradeItemsResponse
}
