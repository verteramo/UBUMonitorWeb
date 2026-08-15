package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCourse
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP de obtención de cursos destacados.
 *
 * @author Marcelo Verteramo Pérsico*/
@ClientProfile("webservice-client")
interface BlockStarredcoursesClient {
  /** Obtiene los curso destacados del usuario autenticado. */
  @PostExchange
  fun getStarredCourses(): List<MoodleCourse>
}
