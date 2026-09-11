/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.core.security.Principal
import es.ubu.lsi.ubumonitorweb.data.api.Completion
import es.ubu.lsi.ubumonitorweb.data.api.Course
import es.ubu.lsi.ubumonitorweb.data.api.Event
import es.ubu.lsi.ubumonitorweb.data.api.Grade
import es.ubu.lsi.ubumonitorweb.data.api.LogEntry
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
   * @param principal Usuario autenticado.
   * @param classification Clasificación de los cursos solicitados.
   * @return Cursos solicitados normalizados.
   */
  @GetMapping("/{classification:all|recent|starred|past|future|inprogress}")
  fun getCourses(
    @AuthenticationPrincipal principal: Principal,
    @PathVariable classification: String,
  ): List<Course> =
    when (classification) {
      "all" -> courseService.getAllCourses(principal.id)
      "recent" -> courseService.getRecentCourses(principal.id)
      "starred" -> courseService.getStarredCourses()
      else -> courseService.getClassifiedCourses(classification)
    }

  @GetMapping("/{id}/users")
  fun getUsers(
    @PathVariable id: Int,
  ): List<User> = courseService.getUsers(id)

  @GetMapping("/{id}/sections")
  fun getSections(
    @PathVariable id: Int,
  ): List<Section> = courseService.getSections(id)

  @GetMapping("/{id}/grades")
  fun getGrades(
    @PathVariable id: Int,
  ): List<Grade> = courseService.getGrades(id)

  @GetMapping("/{id}/events")
  fun getEvents(
    @PathVariable id: Int,
  ): List<Event> = courseService.getEvents(id)

  @GetMapping("/{id}/logs")
  fun getLogs(
    @PathVariable id: Int,
  ): List<LogEntry> = courseService.getLogs(id)

  @GetMapping("{courseId}/completion/{userId}")
  fun getCompletion(
    @PathVariable courseId: Int,
    @PathVariable userId: Int,
  ): List<Completion> = courseService.getCompletion(courseId, userId)
}
