/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.core.client.PhpCollection
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleCategory
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleCourse
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleSection
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP de obtención de cursos, permite obtener categorías,
 * cursos recientes y cursos clasificados (en progreso, pasados y futuros).
 */
@ClientProfile("webservice-client")
interface CoreCourseClient {
  data class ClassifiedCourses(
    val courses: List<MoodleCourse>,
  )

  /** Obtiene los cursos recientes para el ID de usuario especificado. */
  @PostExchange
  fun getRecentCourses(
    @RequestParam userid: Int,
  ): List<MoodleCourse>

  /** Obtiene los cursos clasificados para el usuario autenticado. */
  @PostExchange
  fun getEnrolledCoursesByTimelineClassification(
    @RequestParam classification: String,
  ): ClassifiedCourses

  /** Obtiene las categorías de cursos que cumplan con los criterios especificados. */
  @PostExchange
  fun getCategories(
    @PhpCollection criteria: List<Pair<String, Any>>,
  ): List<MoodleCategory>

  /** Obtiene los contenidos (lista de secciones) para el ID del curso especificado. */
  @PostExchange
  fun getContents(
    @RequestParam courseid: Int,
  ): List<MoodleSection>
}
