package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.DispatcherType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration(
  private val properties: SecurityProperties,
) {
  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? = config.authenticationManager

  @Bean
  fun securityFilterChain(security: HttpSecurity): SecurityFilterChain =
    security
      .csrf {
        it.disable()
      }.authorizeHttpRequests {
        it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
        it.requestMatchers(*properties.publicRoutes.toTypedArray()).permitAll()
        it.anyRequest().authenticated()
      }.build()
}
