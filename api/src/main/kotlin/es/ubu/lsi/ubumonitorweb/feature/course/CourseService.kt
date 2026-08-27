/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.data.api.Course
import es.ubu.lsi.ubumonitorweb.data.api.Section
import es.ubu.lsi.ubumonitorweb.data.api.User
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleCategory
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleCourse
import es.ubu.lsi.ubumonitorweb.feature.course.client.BlockStarredcoursesClient
import es.ubu.lsi.ubumonitorweb.feature.course.client.CoreCourseClient
import es.ubu.lsi.ubumonitorweb.feature.course.client.CoreEnrolClient
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices

/**
 * Servicio de obtención de cursos. Este servicio es la fuente de verdad o pool de cursos, sus
 * métodos realizan toda la "fontanería" necesaria para unificar cursos con categorías,
 * construyendo los criterios de solicitud y manteniendo un mapa en memoria de las categorías
 * solicitadas al servicio, para evitar la sobrecarga y reducir el tiempo de espera.
 */
@Service
@ImportHttpServices(
  BlockStarredcoursesClient::class,
  CoreCourseClient::class,
  CoreEnrolClient::class,
)
class CourseService {
  private val blockStarredcoursesClient: BlockStarredcoursesClient
  private val coreCourseClient: CoreCourseClient
  private val coreEnrolClient: CoreEnrolClient

  constructor(
    blockStarredcoursesClient: BlockStarredcoursesClient,
    coreCourseClient: CoreCourseClient,
    coreEnrolClient: CoreEnrolClient,
  ) {
    this.blockStarredcoursesClient = blockStarredcoursesClient
    this.coreCourseClient = coreCourseClient
    this.coreEnrolClient = coreEnrolClient
    this.categories = mutableMapOf<Any, MoodleCategory>()
  }

  /** Mapa de categorías solicitadas al webservice de Moodle. */
  private val categories: MutableMap<Any, MoodleCategory>

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
     * Se seleccionan aquellas categorías que no existen previamente
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
  ): List<Course> { // Precarga de las categorías en memoria
    prefetchCategories(field, courses.mapNotNull(selector))

    // Mapeo de cursos en formato Moodle a formato normalizado.
    return courses.mapNotNull { course ->
      selector(course)?.let { categories[it] }?.let { course.toCourse(it) }
    }
  }

  /**
   * Obtiene todos los cursos correspondientes al identificador de usuario especificado.
   *
   * @param id Identificador de usuario.
   * @return Lista de cursos normalizados.
   */
  fun getAllCourses(id: Int): List<Course> =
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
  fun getClassifiedCourses(classification: String): List<Course> =
    normalize(
      coreCourseClient.getEnrolledCoursesByTimelineClassification(classification).courses,
      "name",
    ) {
      it.coursecategory
    }

  fun getUsers(id: Int): List<User> = coreEnrolClient.getEnrolledUsers(id).map { it.toUser() }

  fun getSections(id: Int): List<Section> = coreCourseClient.getContents(id).map { it.toSection() }
}
