package es.ubu.lsi.ubumonitorweb.feature.user

import es.ubu.lsi.ubumonitorweb.feature.user.dto.MoodleUser
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
  private val userService: UserService,
) {
  @GetMapping
  fun getUsersByCourseId(
    @RequestParam courseId: Int,
  ): List<MoodleUser> = userService.getUsersByCourseId(courseId)

  @GetMapping("/icon/{id}/{size:f1|f2|f3}", produces = [MediaType.IMAGE_PNG_VALUE])
  fun getUserIcon(
    @PathVariable id: Int,
    @PathVariable size: String,
  ): ByteArray? = userService.getUserIcon(id, size)
}
