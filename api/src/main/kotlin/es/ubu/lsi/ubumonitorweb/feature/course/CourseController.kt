/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.core.moodle.SiteInfo
import es.ubu.lsi.ubumonitorweb.data.api.Course
import es.ubu.lsi.ubumonitorweb.data.api.Section
import es.ubu.lsi.ubumonitorweb.data.api.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlado público que proporciona los cursos del usuario autenticado.
 */
@RestController
@RequestMapping("/api/courses")
class CourseController(
  private val courseService: CourseService,
) {
  /**
   * Obtiene los cursos, según clasificación, del usuario autenticado.
   *
   * @param siteInfo Usuario autenticado.
   * @param classification Clasificación de los cursos solicitados.
   * @return Cursos solicitados normalizados.
   */
  @GetMapping("/{classification:all|recent|starred|past|future|inprogress}")
  fun getCourses(
    @AuthenticationPrincipal siteInfo: SiteInfo,
    @PathVariable classification: String,
  ): List<Course> =
    when (classification) {
      "all" -> courseService.getAllCourses(siteInfo.userid)
      "recent" -> courseService.getRecentCourses(siteInfo.userid)
      "starred" -> courseService.getStarredCourses()
      else -> courseService.getClassifiedCourses(classification)
    }

  @GetMapping("/users/{id}")
  fun getUsers(
    @PathVariable id: Int,
  ): List<User> = courseService.getUsers(id)

  @GetMapping("/sections/{id}")
  fun getSections(
    @PathVariable id: Int,
  ): List<Section> = courseService.getSections(id)
}
