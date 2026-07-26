package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Clase que define el bean [SecurityFilterChain] de la aplicación.
 *
 * @param authFilter Filtro de autenticación.
 * @param authFilterProperties Propiedades del filtro de autenticación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Configuration
class AuthConfig(
    private val authFilter: AuthFilter,
    private val authFilterProperties: AuthFilter.Properties,
) {

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
      it.requestMatchers(
        *authFilterProperties.publicRoutes.toTypedArray(),
      ).permitAll()
      it.anyRequest().authenticated()
    }.addFilterBefore(
      authFilter, UsernamePasswordAuthenticationFilter::class.java,
    ).build()
  }
}
