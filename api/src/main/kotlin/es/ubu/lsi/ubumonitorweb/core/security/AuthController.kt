/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.AuthConfig
import es.ubu.lsi.ubumonitorweb.domain.Principal
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
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
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
  private val authManager: AuthenticationManager,
  private val authService: AuthService,
) {
  /**
   * Parámetros de login.
   */
  data class UsernamePasswordLoginParams(
    val username: String,
    val password: String,
  )

  data class TokenLoginParams(
    val token: String,
  )

  private fun HttpSessionSecurityContextRepository.saveAuthentication(
    authentication: Authentication,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) {
    saveContext(
      SecurityContextHolder
        .createEmptyContext()
        .also { it.authentication = authentication }
        .also { SecurityContextHolder.setContext(it) },
      request,
      response,
    )
  }

  /**
   * Repositorio de la sesión HTTP en memoria.
   */
  private val sessionRepository = HttpSessionSecurityContextRepository()

  @GetMapping("/principal")
  fun getPrincipal(
    @AuthenticationPrincipal principal: Principal,
  ): Principal = principal

  @GetMapping("/discover")
  fun discover(): AuthConfig? = authService.discover()

  /**
   * Realiza el inicio de sesión.
   */
  @PostMapping("/login")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody loginParams: UsernamePasswordLoginParams,
  ): Principal =
    authManager.authenticate(UsernamePasswordAuthenticationToken(loginParams.username, loginParams.password)).let {
      sessionRepository.saveAuthentication(it, request, response)
      it.principal as Principal
    }

  @PostMapping("/login-sso")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody loginParams: TokenLoginParams,
  ): Principal =
    authManager.authenticate(SsoAuthenticationToken(loginParams.token)).let {
      sessionRepository.saveAuthentication(it, request, response)
      it.principal as Principal
    }

  /**
   * Realiza el cierre de sesión.
   */
  @GetMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun logout(
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) = SecurityContextHolder.getContext().authentication.let {
    SecurityContextLogoutHandler().logout(request, response, it)
  }
}
