package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/session")
class SessionController(
  private val userService: UserService,
) {
  /**
   * Obtiene los datos del usuario autenticado en la sesión actual.
   *
   * @param user Usuario autenticado.
   * @return Datos de perfil del usuario activo.
   */
  @GetMapping("/user")
  fun getUser(
    @AuthenticationPrincipal user: User,
  ): User = user

  /**
   * Actualiza las preferencias del usuario activo en sesión y las persiste en el sistema de archivos.
   *
   * @param user Usuario activo inyectado desde la sesión.
   * @param preferences Nuevas preferencias enviadas en el cuerpo JSON.
   * @param request Petición HTTP actual.
   * @param response Respuesta HTTP actual.
   */
  @PutMapping("/preferences")
  fun putPreferences(
    @AuthenticationPrincipal user: User,
    @RequestBody preferences: Map<String, Any>,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) {
    userService.findOrCreate(user.email, user.siteUrl, user.id).let {
      userService.setPreferences(it, preferences)
      userService.save(it)
    }

    user.preferences.clear()
    user.preferences.putAll(preferences)
  }
}
