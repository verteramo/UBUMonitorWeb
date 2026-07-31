package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.exception.MoodleException
import jakarta.servlet.DispatcherType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import tools.jackson.databind.ObjectMapper

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration(private val properties: SecurityProperties) {

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? {
    return config.authenticationManager
  }

  @Bean
  fun securityFilterChain(
      security: HttpSecurity,
      authEntryPoint: AuthenticationEntryPoint,
      loginSuccessHandler: AuthenticationSuccessHandler,
      loginFailureHandler: AuthenticationFailureHandler,
      logoutSuccessHandler: LogoutSuccessHandler,
  ): SecurityFilterChain {
    return security.csrf {
      it.disable()
    }.authorizeHttpRequests {
      it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
      it.requestMatchers(*properties.publicRoutes.toTypedArray()).permitAll()
      it.anyRequest().authenticated()
    }.exceptionHandling {
      it.authenticationEntryPoint(authEntryPoint)
    }.formLogin {
      it.successHandler(loginSuccessHandler)
      it.failureHandler(loginFailureHandler)
      properties.loginUrl?.let(it::loginProcessingUrl)
    }.logout {
      it.logoutSuccessHandler(logoutSuccessHandler)
      properties.logoutUrl?.let(it::logoutUrl)
    }.build()
  }

  @Bean
  fun loginSuccessHandler(mapper: ObjectMapper): AuthenticationSuccessHandler {
    return AuthenticationSuccessHandler { request, response, authentication ->
      response.status = HttpStatus.OK.value()
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      mapper.writeValue(response.writer, authentication.principal)
    }
  }

  @Bean
  fun loginFailureHandler(mapper: ObjectMapper): AuthenticationFailureHandler {
    return AuthenticationFailureHandler { request, response, exception ->
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      (exception.cause as? MoodleException)?.let {
        mapper.writeValue(
          response.writer,
          ProblemDetail.forStatusAndDetail(it.status, it.message),
        )
      }
    }
  }

  @Bean
  fun logoutSuccessHandler(): LogoutSuccessHandler {
    return HttpStatusReturningLogoutSuccessHandler()
  }

  @Bean
  fun authEntryPoint(): AuthenticationEntryPoint {
    return HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
  }
}
