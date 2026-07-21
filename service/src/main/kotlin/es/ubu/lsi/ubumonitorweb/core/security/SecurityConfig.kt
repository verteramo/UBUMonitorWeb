package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Clase que define el bean [SecurityFilterChain] de la aplicación. Entre otras cosas, define las
 * rutas públicas, las rutas protegidas y la gestión de tokens JWE.
 *
 * @param jweFilter Filtro de solicitudes para la gestión de tokens JWE.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(private val jweFilter: JweFilter) {

  companion object {
    /** Rutas públicas */
    val PUBLIC_ROUTES = arrayOf(
      "/api/token",
      "/actuator/**",
    )
  }

  /**
   * Define el bean con la cadena de filtros de seguridad.
   *
   * @param http Objeto de configuración HTTP.
   * @return Cadena de filtros de seguridad.
   */
  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    return http.csrf {
      it.disable()
    }.sessionManagement {
      it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }.authorizeHttpRequests {
      it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
      it.requestMatchers(*PUBLIC_ROUTES).permitAll()
      it.anyRequest().authenticated()
    }.addFilterBefore(
      jweFilter, UsernamePasswordAuthenticationFilter::class.java,
    ).build()
  }
}
