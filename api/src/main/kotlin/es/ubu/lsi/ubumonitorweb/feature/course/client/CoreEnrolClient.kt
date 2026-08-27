/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpCollection
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleCourse
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleUser
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP de obtención de:
 * - Todos los cursos de un usuario determinado
 * - Usuarios matriculados en un curso determinado
 */
@ClientProfile("webservice-client")
interface CoreEnrolClient {
  /** Obtiene todos los cursos para el ID de usuario especificado. */
  @PostExchange
  fun getUsersCourses(
    @RequestParam userid: Int,
    @RequestParam returnusercount: Int = 0,
  ): List<MoodleCourse>

  /** Obtiene los usuarios matriculados para el ID del curso especificado. */
  @PostExchange
  fun getEnrolledUsers(
    @RequestParam courseid: Int,
    @PhpCollection(keyName = "name") options: List<Pair<String, Any>> = emptyList(),
  ): List<MoodleUser>
}
