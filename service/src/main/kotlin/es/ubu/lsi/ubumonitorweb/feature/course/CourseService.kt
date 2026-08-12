package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.feature.course.api.Course
import es.ubu.lsi.ubumonitorweb.feature.course.client.BlockStarredcoursesClient
import es.ubu.lsi.ubumonitorweb.feature.course.client.CoreCourseClient
import es.ubu.lsi.ubumonitorweb.feature.course.client.CoreEnrolClient
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCategory
import es.ubu.lsi.ubumonitorweb.feature.course.dto.MoodleCourse
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Servicio de obtención de cursos. Este servicio es la fuente de verdad o pool de cursos, sus
 * métodos realizan toda la "fontanería" necesaria para unificar cursos con categorías,
 * construyendo los criterios de solicitud y manteniendo un mapa en memoria de las categorías
 * solicitadas al servicio, para evitar la sobrecarga y reducir el tiempo de espera.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Service
@ImportHttpServices(
  BlockStarredcoursesClient::class,
  CoreCourseClient::class,
  CoreEnrolClient::class,
)
class CourseService(
  private val blockStarredcoursesClient: BlockStarredcoursesClient,
  private val coreCourseClient: CoreCourseClient,
  private val coreEnrolClient: CoreEnrolClient,
) {
  /** Mapa de categorías solicitadas al webservice de Moodle. */
  private val categories = mutableMapOf<Any, MoodleCategory>()

  /**
   * Precarga de categorías.
   *
   * @param field Campo de la solicitud, podría ser "id", "name" o el que fuera.
   * @param values Lista de valores para la construcción de los criterios.
   */
  private fun prefetchCategories(
    field: String,
    values: List<Any>,
  ) {
    /*
     * Se selecciona aquellas categorías que no existen previamente
     * en el mapa de categorías en memoria.
     */
    val missingValues = values.distinct().filter { !categories.containsKey(it) }

    if (missingValues.isNotEmpty()) {
      /*
       * Se mapean las categorías como pares de clave-valor para
       * pasárselos como argumento al cliente HTTP.
       */
      val params = missingValues.map { field to it }

      // Realización de la solicitud y almacenamiento en el mapa
      coreCourseClient.getCategories(params).forEach { category ->
        categories[category.id] = category
        categories[category.name] = category
      }
    }
  }

  /**
   * Normaliza los cursos asignándoles su categoría correspondiente.
   *
   * @param courses Lista de cursos en el formato de devolución de Moodle.
   * @param field Nombre del campo de solicitud.
   * @param selector Función lambda que obtiene el valor del campo.
   */
  private inline fun normalize(
    courses: List<MoodleCourse>,
    field: String,
    crossinline selector: (MoodleCourse) -> Any?,
  ): List<Course> {
    // Precarga de las categorías en memoria
    prefetchCategories(field, courses.mapNotNull(selector))

    // Mapeo de cursos en formato Moodle a formato normalizado.
    return courses.mapNotNull { course ->
      selector(course)?.let { categories[it] }?.let { course.normalize(it) }
    }
  }

  /**
   * Obtiene todos los cursos correspondientes al identificador de usuario especificado.
   *
   * @param id Identificador de usuario.
   * @return Lista de cursos normalizados.
   */
  fun getCourses(id: Int): List<Course> =
    normalize(coreEnrolClient.getUsersCourses(id), "id") {
      it.category
    }

  /**
   * Obtiene los cursos recientes correspondientes al identificador de usuario especificado.
   *
   * @param id Identificador de usuario.
   * @return Lista de cursos normalizados.
   */
  fun getRecentCourses(id: Int): List<Course> =
    normalize(coreCourseClient.getRecentCourses(id), "name") {
      it.coursecategory
    }

  /**
   * Obtiene los cursos destacados correspondientes al usuario autenticado.
   *
   * @return Lista de cursos normalizados.
   */
  fun getStarredCourses(): List<Course> =
    normalize(blockStarredcoursesClient.getStarredCourses(), "name") {
      it.coursecategory
    }

  /**
   * Obtiene los cursos según clasificación:
   * - `inprogress`
   * - `past`
   * - `future`
   * Correspondientes al usuario autenticado.
   *
   * @return Lista de cursos normalizados.
   */
  fun getByClassification(classification: String): List<Course> =
    normalize(
      coreCourseClient.getEnrolledCoursesByTimelineClassification(classification).courses,
      "name",
    ) {
      it.coursecategory
    }
}
