package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.DispatcherType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration(private val properties: SecurityProperties) {

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
    return config.authenticationManager
  }

  @Bean
  fun securityFilterChain(security: HttpSecurity): SecurityFilterChain {
    return security.csrf {
      it.disable()
    }.authorizeHttpRequests {
      it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
      it.requestMatchers(*properties.publicRoutes.toTypedArray()).permitAll()
      it.anyRequest().authenticated()
    }.logout {
      it.logoutUrl(properties.logoutUrl)
      it.logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
    }.build()
  }
}
