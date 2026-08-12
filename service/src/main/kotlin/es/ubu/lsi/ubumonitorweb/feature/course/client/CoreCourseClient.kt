package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpCollection
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCategory
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleClassifiedCourses
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCourse
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP de obtención de cursos, permite obtener categorías,
 * cursos recientes y cursos clasificados (en progreso, pasados y futuros).
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@ClientProfile("webservice")
interface CoreCourseClient {
  /** Obtiene los cursos recientes para el identificador de usuario especificado. */
  @PostExchange
  fun getRecentCourses(
    @RequestParam userid: Int,
  ): List<MoodleCourse>

  /** Obtiene los cursos clasificados para el usuario autenticado. */
  @PostExchange
  fun getEnrolledCoursesByTimelineClassification(
    @RequestParam classification: String,
  ): MoodleClassifiedCourses

  /** Obtiene las categorías de cursos que cumplan con los criterios especificados. */
  @PostExchange
  fun getCategories(
    @PhpCollection criteria: List<Pair<String, Any>>,
  ): List<MoodleCategory>
}
