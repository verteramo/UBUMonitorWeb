package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.core.moodle.SiteInfo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlado público que proporciona los cursos del usuario autenticado.
 *
 * @author Marcelo Verteramo Pérsico
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
  ) = when (classification) {
    "all" -> courseService.getCourses(siteInfo.userid)
    "recent" -> courseService.getRecentCourses(siteInfo.userid)
    "starred" -> courseService.getStarredCourses()
    else -> courseService.getByClassification(classification)
  }

  @GetMapping("/contents/{id}")
  fun getContents(
    @PathVariable id: Int,
  ): Any = courseService.getContents(id)
}
