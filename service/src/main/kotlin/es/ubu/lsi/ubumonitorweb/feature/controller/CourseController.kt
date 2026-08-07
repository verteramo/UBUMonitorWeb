package es.ubu.lsi.ubumonitorweb.feature.controller

import es.ubu.lsi.ubumonitorweb.core.security.MoodlePrincipalDto
import es.ubu.lsi.ubumonitorweb.feature.dto.NormalizedCourse
import es.ubu.lsi.ubumonitorweb.feature.service.BlockStarredcoursesService
import es.ubu.lsi.ubumonitorweb.feature.service.CoreCourseService
import es.ubu.lsi.ubumonitorweb.feature.service.CoreEnrolService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.service.registry.ImportHttpServices

enum class Classification { ALL, STARRED, RECENT, INPROGRESS, PAST, FUTURE }

@RestController
@RequestMapping("/api/course")
@ImportHttpServices(
  CoreEnrolService::class,
  CoreCourseService::class,
  BlockStarredcoursesService::class,
)
class CourseController(
  private val coreEnrolService: CoreEnrolService,
  private val coreCourseService: CoreCourseService,
  private val blockStarredcoursesService: BlockStarredcoursesService,
) {
  @GetMapping("/{classification}")
  fun getByClassification(
    @AuthenticationPrincipal principal: MoodlePrincipalDto,
    @PathVariable classification: Classification,
  ): List<NormalizedCourse> =
    when (classification) {
      Classification.ALL -> {
        coreEnrolService.getUsersCourses(principal.userid).map { course ->
          val category = coreCourseService.getCategories("id", course.category.toString()).first()
          course.normalize(category)
        }
      }

      // coursecategory
      Classification.RECENT -> {
        coreCourseService.getRecentCourses().map { course ->
          val category = coreCourseService.getCategories("name", course.coursecategory).first()
          course.normalize(category)
        }
      }

      // coursecategory
      Classification.STARRED -> {
        blockStarredcoursesService.getStarredCourses().map { course ->
          val category = coreCourseService.getCategories("name", course.coursecategory).first()
          course.normalize(category)
        }
      }

      // coursecategory
      else -> {
        coreCourseService
          .getEnrolledCoursesByTimelineClassification(
            classification.name.lowercase(),
          ).courses
          .map { course ->
            val category = coreCourseService.getCategories("name", course.coursecategory).first()
            course.normalize(category)
          }
      }
    }
}
