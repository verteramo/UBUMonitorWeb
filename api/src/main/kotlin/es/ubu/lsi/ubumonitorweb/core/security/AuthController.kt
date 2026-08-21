package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.moodle.Principal
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
 * @author Marcelo Verteramo Pérsico
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
  private val manager: AuthenticationManager,
) {
  data class Credentials(
    val username: String,
    val password: String,
  )

  /** Repositorio de la sesión HTTP en memoria. */
  private val session = HttpSessionSecurityContextRepository()

  /** Inicio de sesión. */
  @PostMapping("/login")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody credentials: Credentials,
  ): Principal =
    manager
      .authenticate(
        UsernamePasswordAuthenticationToken(
          credentials.username,
          credentials.password,
        ),
      ).let {
        // Almacenamiento de la sesión HTTP en memoria
        session.saveContext(
          SecurityContextHolder
            .createEmptyContext()
            .apply { authentication = it }
            .apply(SecurityContextHolder::setContext),
          request,
          response,
        )

        it.principal as Principal
      }

  /** Realiza el cierre de sesión. */
  @GetMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun logout(
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) = SecurityContextHolder.getContext().authentication.let {
    SecurityContextLogoutHandler().logout(request, response, it)
  }
}
