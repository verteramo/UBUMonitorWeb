/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpMap
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCategory
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleCourse
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleSection
import org.springframework.web.bind.annotation.RequestParam

/**
 * Cliente HTTP de obtención de cursos, permite obtener categorías,
 * cursos recientes y cursos clasificados (en progreso, pasados y futuros).
 */
@Client("webservice-client")
interface CoreCourseClient {
  data class EnrolledCoursesByTimelineClassificationResponse(
    val courses: List<MoodleCourse>,
  )

  /**
   * Obtiene los cursos recientes para el ID de usuario especificado.
   */
  @Client
  fun getRecentCourses(
    @RequestParam userid: Int,
  ): List<MoodleCourse>

  /**
   * Obtiene los cursos clasificados para el usuario autenticado.
   */
  @Client
  fun getEnrolledCoursesByTimelineClassification(
    @RequestParam classification: String,
  ): EnrolledCoursesByTimelineClassificationResponse

  /**
   * Obtiene las categorías de cursos que cumplan con los criterios especificados.
   */
  @Client
  fun getCategories(
    @PhpMap criteria: List<Pair<String, Any>>,
  ): List<MoodleCategory>

  /**
   * Obtiene los contenidos (lista de secciones) para el ID del curso especificado.
   */
  @Client
  fun getContents(
    @RequestParam courseid: Int,
  ): List<MoodleSection>
}
