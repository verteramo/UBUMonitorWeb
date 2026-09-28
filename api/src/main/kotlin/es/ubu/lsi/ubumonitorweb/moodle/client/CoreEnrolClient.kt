/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpMap
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCourse
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleUser
import org.springframework.web.bind.annotation.RequestParam

/**
 * Cliente HTTP de obtención de:
 * - Todos los cursos de un usuario determinado
 * - Usuarios matriculados en un curso determinado
 */
@Client("webservice-client")
interface CoreEnrolClient {
  /**
   * Obtiene todos los cursos para el ID de usuario especificado.
   */
  @Client
  fun getUsersCourses(
    @RequestParam userid: Int,
    @RequestParam returnusercount: Int = 0,
  ): List<MoodleCourse>

  /**
   * Obtiene los usuarios matriculados para el ID del curso especificado.
   */
  @Client
  fun getEnrolledUsers(
    @RequestParam courseid: Int,
    @PhpMap(keyName = "name") options: List<Pair<String, Any>> = emptyList(),
  ): List<MoodleUser>
}
