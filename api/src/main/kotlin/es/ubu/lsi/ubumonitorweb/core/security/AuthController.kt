/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.AuthConfig
import es.ubu.lsi.ubumonitorweb.domain.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador REST que gestiona el ciclo de vida de la autenticación de usuarios y la sesión HTTP.
 *
 * @property authManager Gestor encargado de delegar y validar credenciales contra los proveedores configurados.
 * @property authService Servicio para la detección y descubrimiento de configuraciones de autenticación externas.
 * @property securityContextRepository Repositorio para persistir y recuperar el contexto de seguridad.
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
  private val authManager: AuthenticationManager,
  private val authService: AuthService,
  private val securityContextRepository: SecurityContextRepository,
) {
  /**
   * Carga útil para la autenticación estándar mediante usuario y contraseña.
   *
   * @property username Identificador de usuario.
   * @property password Clave de acceso del usuario.
   */
  data class CredentialsLoginRequest(
    val username: String,
    val password: String,
  )

  /**
   * Carga útil para la autenticación mediante token SSO.
   *
   * @property token Credencial que contiene los tokens de Moodle codificados.
   */
  data class SsoTokenLoginRequest(
    val token: String,
  )

  /**
   * Estrategia centralizada para acceder y gestionar de forma segura el contexto de seguridad.
   *
   * https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#use-securitycontextholderstrategy
   */
  private val securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

  /**
   * Handler reutilizable para invalidar la sesión y purgar el contexto de seguridad en el logout.
   *
   * https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html#creating-custom-logout-endpoint
   */
  private val logoutHandler = SecurityContextLogoutHandler()

  /**
   * Autentica el token suministrado, asienta el nuevo contexto en la sesión HTTP y extrae el usuario asociado.
   *
   * @param token Credenciales o token de autenticación a verificar.
   * @param request Petición HTTP actual requerida para asociar el contexto a la sesión de servlet.
   * @param response Respuesta HTTP para asentar las cookies de sesión resultantes.
   * @return Usuario autenticado con sus datos de perfil.
   */
  private fun authenticateAndSave(
    token: Authentication,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ): User =
    authManager.authenticate(token).let {
      securityContextHolderStrategy
        .createEmptyContext()
        .apply { authentication = it }
        .apply { securityContextHolderStrategy.context = this }
        .run { securityContextRepository.saveContext(this, request, response) }

      it.principal as User
    }

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
   * Expone la configuración y métodos de autenticación soportados por el servidor.
   *
   * @return Parámetros de descubrimiento del servicio.
   */
  @GetMapping("/discover")
  fun discover(): AuthConfig? = authService.discover()

  /**
   * Inicia sesión validando credenciales de usuario y contraseña.
   *
   * @param request Petición HTTP entrante.
   * @param response Respuesta HTTP.
   * @param params Credenciales enviadas en el cuerpo JSON de la petición.
   * @return Usuario autenticado con su contexto asociado.
   */
  @PostMapping("/login")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody params: CredentialsLoginRequest,
  ): User =
    authenticateAndSave(
      UsernamePasswordAuthenticationToken.unauthenticated(
        params.username,
        params.password,
      ),
      request,
      response,
    )

  /**
   * Inicia sesión validando un token emitido por el servicio de SSO.
   *
   * @param request Petición HTTP entrante.
   * @param response Respuesta HTTP.
   * @param params Token de acceso enviado en el cuerpo JSON de la petición.
   * @return Usuario autenticado con su contexto asociado.
   */
  @PostMapping("/login-sso")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody params: SsoTokenLoginRequest,
  ): User = authenticateAndSave(PreAuthenticatedAuthenticationToken(params.token, null), request, response)

  /**
   * Cierra la sesión activa destruyendo el contexto de seguridad.
   *
   * @param request Petición HTTP.
   * @param response Respuesta HTTP para propagar la invalidación de cookies.
   */
  @GetMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun logout(
    authentication: Authentication,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ) = logoutHandler.logout(request, response, authentication)
}
