/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCourse
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP de obtención de cursos destacados.
 */
@Client("webservice-client")
interface BlockStarredcoursesClient {
  /**
   * Obtiene los curso destacados del usuario autenticado.
   */
  @Client
  fun getStarredCourses(): List<MoodleCourse>
}
