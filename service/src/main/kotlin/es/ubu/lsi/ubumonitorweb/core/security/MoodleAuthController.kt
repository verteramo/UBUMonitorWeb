package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador de los endpoints relacionados con la autenticación.
 *
 * @param manager Manager de autenticación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@RestController
@RequestMapping("/api/auth")
class MoodleAuthController(
  private val manager: AuthenticationManager,
) {
  /** Cuerpo de la solicitud de autenticación. */
  data class AuthRequestBody(
    val username: String,
    val password: String,
  )

  /** Repositorio que almacena el contexto de seguridad mediante sesiones HTTP */
  private val repository = HttpSessionSecurityContextRepository()

  /**
   * Realiza el inicio de sesión.
   *
   * @param request Solicitud.
   * @param response Respuesta.
   * @param body Cuerpo de la solicitud con las credenciales de autenticación.
   * @return En caso de éxito, el objeto Principal autenticado.
   */
  @PostMapping("/login")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody body: AuthRequestBody,
  ): MoodlePrincipal {
    // Desestructuración del cuerpo en valores planos
    val (username, password) = body

    // Solicitud de autenticación al manager, quien delega en el MoodleAuthProvider
    val authentication =
      manager.authenticate(
        UsernamePasswordAuthenticationToken(username, password),
      ) as MoodleAuthToken

    // Creación del nuevo contexto y almacenamiento en la sesión HTTP en memoria
    val context = SecurityContextHolder.createEmptyContext()
    context.authentication = authentication
    SecurityContextHolder.setContext(context)
    repository.saveContext(context, request, response)

    // Devolución del Principal
    return authentication.principal
  }

  /**
   * Realiza el cierre de sesión.
   *
   * @param request Solicitud.
   * @param response Respuesta.
   * @return Respuesta vacía con estado successful `204 No Content`.
   */
  @GetMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun logout(
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication != null) {
      SecurityContextLogoutHandler().logout(request, response, authentication)
    }
  }

  /**
   * Devuelve los datos del usuario autenticado.
   *
   * @param principal Inyección del usuario autenticado.
   * @return Usuario autenticado.
   */
  @GetMapping("/principal")
  fun principal(
    @AuthenticationPrincipal principal: MoodlePrincipal,
  ) = principal
}
