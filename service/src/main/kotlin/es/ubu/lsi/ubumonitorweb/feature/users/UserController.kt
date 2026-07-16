//package es.ubu.lsi.ubumonitorweb.feature.users
//
//import es.ubu.lsi.ubumonitorweb.core.base.MoodleRestController
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//
//@MoodleRestController("/users")
//class UserController(private val userService: CoreUserService) {
//
//  @GetMapping("/{id}")
//  fun getUserById(@PathVariable id: String): Any =
//    userService.getUsersByField(FindParams("id", id))
//}
