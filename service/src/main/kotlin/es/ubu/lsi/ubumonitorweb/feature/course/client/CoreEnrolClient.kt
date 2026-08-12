package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCourse
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP de obtención de todos los cursos de un usuario determinado.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@ClientProfile("webservice")
interface CoreEnrolClient {
  /** Obtiene todos los cursos para el identificador de usuario especificado. */
  @PostExchange
  fun getUsersCourses(
    @RequestParam userid: Int,
    @RequestParam returnusercount: Int = 0,
  ): List<MoodleCourse>
}
