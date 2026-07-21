package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.feature.token.MoodleToken
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(private val jweService: JweService) : OncePerRequestFilter() {

  private val log = KotlinLogging.logger {}

  companion object {
    private const val BEARER_PREFIX = "Bearer "
    private val ANT_PATH_MATCHER = AntPathMatcher()
  }

  override fun shouldNotFilter(request: HttpServletRequest): Boolean {
    return SecurityConfig.PUBLIC_ROUTES.any { ANT_PATH_MATCHER.match(it, request.servletPath) }
  }

  override fun doFilterInternal(
      request: HttpServletRequest,
      response: HttpServletResponse,
      filterChain: FilterChain,
  ) {
    request.getJwtToken()?.let { token ->
      try {
        val moodleToken = jweService.extract(token, MoodleToken::class)
        val auth = UsernamePasswordAuthenticationToken(moodleToken, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth
      }
      catch (e: Exception) {
        log.debug(e) { "Error al procesar el token JWT en la ruta ${request.servletPath}: ${e.message}" }
        SecurityContextHolder.clearContext()
      }
    }

    filterChain.doFilter(request, response)
  }

  private fun HttpServletRequest.getJwtToken(): String? {
    return getHeader(HttpHeaders.AUTHORIZATION)
        ?.takeIf { it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
  }

}
