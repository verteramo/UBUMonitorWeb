package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtFilter: JwtFilter) {

  companion object {
    val PUBLIC_ROUTES = arrayOf(
      "/api/token",
      "/actuator/**",
    )
  }

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
      http.csrf {
        it.disable()
      }.sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }.authorizeHttpRequests {
        it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
        it.requestMatchers(*PUBLIC_ROUTES).permitAll()
        it.anyRequest().authenticated()
      }.addFilterBefore(
        jwtFilter, UsernamePasswordAuthenticationFilter::class.java,
      ).build()
}
