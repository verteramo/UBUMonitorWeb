/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.domain.SiteConfig
import es.ubu.lsi.ubumonitorweb.domain.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador REST que gestiona el ciclo de vida de la autenticación de usuarios y la sesión HTTP.
 *
 * @property authService Servicio para la detección y descubrimiento de configuraciones de autenticación externas.
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
  private val authService: AuthService,
  private val siteService: SiteService,
  private val securityContextService: SecurityContextService,
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
   * @property token Token principal codificado recibido desde el deep link.
   */
  data class SsoTokenLoginRequest(
    val token: String,
  )

  data class LoginResponse(
    val user: User,
    val preferences: Map<String, Any>,
  )

  /**
   * Expone la configuración y métodos de autenticación soportados por el servidor.
   *
   * @return Parámetros de descubrimiento del servicio.
   */
  @GetMapping("/discover")
  fun discover(): SiteConfig? = siteService.discover()

  /**
   * Inicia sesión validando credenciales de usuario y contraseña.
   *
   * @param request Petición HTTP entrante.
   * @param response Respuesta HTTP.
   * @param credentials Credenciales enviadas en el cuerpo JSON de la petición.
   * @return Usuario autenticado con su contexto asociado.
   */
  @PostMapping("/login")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody credentials: CredentialsLoginRequest,
  ): LoginResponse =
    authService
      .authenticate(
        UsernamePasswordAuthenticationToken.unauthenticated(
          credentials.username,
          credentials.password,
        ),
      ).let { (user, keyring, preferences) ->
        securityContextService.create(user, keyring, request, response)
        LoginResponse(user, preferences)
      }

  /**
   * Inicia sesión validando un token emitido por el servicio de SSO.
   *
   * @param request Petición HTTP entrante.
   * @param response Respuesta HTTP.
   * @param ssoToken Token de acceso enviado en el cuerpo JSON de la petición.
   * @return Usuario autenticado con su contexto asociado.
   */
  @PostMapping("/login-sso")
  fun login(
    request: HttpServletRequest,
    response: HttpServletResponse,
    @RequestBody ssoToken: SsoTokenLoginRequest,
  ): LoginResponse =
    authService
      .authenticate(
        PreAuthenticatedAuthenticationToken(
          ssoToken.token,
          siteService.passport,
        ),
      ).let { (user, keyring, preferences) ->
        securityContextService.create(user, keyring, request, response)
        LoginResponse(user, preferences)
      }

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
  ) = securityContextService.destroy(authentication, request, response)
}
