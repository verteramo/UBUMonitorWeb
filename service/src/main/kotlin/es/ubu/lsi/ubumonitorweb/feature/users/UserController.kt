package es.ubu.lsi.ubumonitorweb.feature.users

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val coreUserService: CoreUserService) {

  @GetMapping("/{id}")
  fun getUserById(@PathVariable id: String): Any =
    coreUserService.getUsersByField(FindParams("id", id))
}
