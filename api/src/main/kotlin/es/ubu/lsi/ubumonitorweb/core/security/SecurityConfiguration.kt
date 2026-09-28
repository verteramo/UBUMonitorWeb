/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.DispatcherType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository

/**
 * Configuración central de seguridad web, gestión de autenticación y autorización de peticiones HTTP.
 *
 * @property properties Propiedades de configuración de seguridad y rutas del sistema.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration(
  private val properties: SecurityProperties,
) {
  /**
   * Repositorio de contexto de seguridad que utiliza el contenedor.
   *
   * @return Repositorio estándar basado en la sesión HTTP de Tomcat.
   */
  @Bean
  fun securityContextRepository(): SecurityContextRepository = HttpSessionSecurityContextRepository()

  /**
   * Gestor de autenticación configurado con los proveedores disponibles en el contexto.
   *
   * @param providers Lista de proveedores de autenticación registrados.
   * @return Gestor [ProviderManager] inicializado con la cadena de proveedores.
   */
  @Bean
  fun providerManager(providers: List<AuthenticationProvider>) =
    ProviderManager(providers).apply { isEraseCredentialsAfterAuthentication = false }

  /**
   * Cadena de filtros de seguridad HTTP, reglas de acceso a rutas y control de excepciones.
   *
   * @param security Constructor de configuración de seguridad web.
   * @return Cadena de filtros [SecurityFilterChain] ensamblada.
   */
  @Bean
  fun securityFilterChain(
    security: HttpSecurity,
    problemDetailAuthEntryPoint: ProblemDetailAuthEntryPoint,
  ): SecurityFilterChain =
    security
      .csrf {
        it.disable()
      }.authorizeHttpRequests {
      /*
       * Se permite la ruta estándar de manejo de errores '/error',
       * y todas aquellas rutas incluidas en la propiedad de configuración 'public-routes'.
       */
        it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
        it.requestMatchers(*properties.publicRoutes.toTypedArray()).permitAll()
        it.anyRequest().authenticated()
      }.exceptionHandling {
      /*
       * Por defecto, Spring Boot incluye el Http403ForbiddenEntryPoint, que devuelve errores 403 en solicitudes
       * anónimas, sin embargo, esta API requiere emitir un '401 Unauthorized' estructurado en formato 'ProblemDetail'.
       */
        it.authenticationEntryPoint(problemDetailAuthEntryPoint)
      }.build()
}
