package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filtro de autenticación. Recibe un [AuthExtractor] que conoce cómo extraer un [Authentication]
 * desde la solicitud, si está presente, el cual se inyecta en el contexto de seguridad.
 *
 * @param extractor Extractor de objetos [Authentication] de la solicitud.
 * @param properties Propiedades del filtro.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class AuthFilter(
    private val extractor: AuthExtractor<*>,
    private val properties: Properties,
) : OncePerRequestFilter() {

  /**
   * Propiedades de configuración.
   *
   * @param publicRoutes Conjunto de rutas públicas que el filtro debe ignorar.
   */
  @ConfigurationProperties("security.filter")
  data class Properties(val publicRoutes: Set<String> = emptySet())

  /** Comparador de rutas */
  private val matcher = AntPathMatcher()

  /**
   * Determina si una ruta debe ser filtrada.
   *
   * @param request Solicitud.
   * @return `true` si la ruta debe ser ignorada.
   */
  override fun shouldNotFilter(request: HttpServletRequest): Boolean {
    return properties.publicRoutes.any { matcher.match(it, request.servletPath) }
  }

  /**
   * Ejecuta el filtro sobre la solicitud.
   *
   * @param request Solicitud.
   * @param response Respuesta.
   * @param filterChain Cadena de filtros.
   */
  override fun doFilterInternal(
      request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain,
  ) {
    extractor.extract(request)?.let {
      SecurityContextHolder.getContext().authentication = it
    } ?: SecurityContextHolder.clearContext()

    filterChain.doFilter(request, response)
  }
}
