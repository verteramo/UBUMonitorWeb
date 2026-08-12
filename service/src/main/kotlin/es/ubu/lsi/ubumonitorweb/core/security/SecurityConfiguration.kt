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

/**
 * Configuración de seguridad.
 *
 * Declara en bean que provee el objeto [AuthenticationManager].
 * Declara el bean que provee la cadena de filtros de seguridad, [SecurityFilterChain].
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration(
  private val properties: SecurityProperties,
) {
  /** Obtiene el objeto [AuthenticationManager]. */
  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? = config.authenticationManager

  /** Obtiene el objeto [SecurityFilterChain]. */
  @Bean
  fun securityFilterChain(security: HttpSecurity): SecurityFilterChain =
    security
      .csrf {
        it.disable()
      }.authorizeHttpRequests {
        /*
         * Se permite la ruta estándar de manejo de errores, /error,
         * y todas aquellas declaradas en la variable de configuración
         * public-routes.
         */
        it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
        it.requestMatchers(*properties.publicRoutes.toTypedArray()).permitAll()
        /*
         * El resto de rutas requieren un usuario autenticado, en caso
         * contrario fallarán con el error 403 Forbidden.
         */
        it.anyRequest().authenticated()
      }.build()
}
