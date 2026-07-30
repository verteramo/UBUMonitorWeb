package es.ubu.lsi.ubumonitorweb.feature.controller

import es.ubu.lsi.ubumonitorweb.feature.service.CoreUserService
import es.ubu.lsi.ubumonitorweb.feature.service.FindParams
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val coreUserService: CoreUserService) {

  @GetMapping
  fun hello() = "Hello World"

  @GetMapping("/{field}={value}")
  fun getUserById(
      @PathVariable field: String,
      @PathVariable value: String,
  ): Any {
    return coreUserService.getUsersByField(FindParams(field, value))
  }
}
