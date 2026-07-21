package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.feature.token.MoodleToken
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
class JweFilter(private val jweService: JweService) : OncePerRequestFilter() {

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
    request.getBearerToken()?.let { token ->
      try {
        val moodleToken = jweService.extract<MoodleToken>(token)
        val auth = UsernamePasswordAuthenticationToken(moodleToken, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth
      }
      catch (e: Exception) {
        //log.debug(e) { Message.ERROR_SEC_JWE(request.servletPath, e.message ?: "") }
        SecurityContextHolder.clearContext()
      }
    }

    filterChain.doFilter(request, response)
  }

  private fun HttpServletRequest.getBearerToken(): String? {
    return getHeader(HttpHeaders.AUTHORIZATION)
        ?.takeIf { it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
  }

}
