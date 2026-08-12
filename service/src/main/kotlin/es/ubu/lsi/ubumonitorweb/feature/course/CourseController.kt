package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.core.security.MoodlePrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlado público que proporciona los cursos del usuario autenticado.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@RestController
@RequestMapping("/api/course")
class CourseController(
  private val courseService: CourseService,
) {
  /**
   * Obtiene los cursos, según clasificación, del usuario autenticado.
   *
   * @param principal Usuario autenticado.
   * @param classification Clasificación de los cursos solicitados.
   * @return Cursos solicitados normalizados.
   */
  @GetMapping("/{classification:all|recent|starred|past|future|inprogress}")
  fun getCourses(
    @AuthenticationPrincipal principal: MoodlePrincipal,
    @PathVariable classification: String,
  ) = when (classification) {
    "all" -> courseService.getCourses(principal.userid)
    "recent" -> courseService.getRecentCourses(principal.userid)
    "starred" -> courseService.getStarredCourses()
    else -> courseService.getByClassification(classification)
  }
}
