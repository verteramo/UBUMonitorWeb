package es.ubu.lsi.ubumonitorweb.core.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver
import org.springframework.web.service.registry.HttpServiceGroupConfigurer
import java.io.InputStream

/**
 * Configurador de clientes [HttpExchange].
 *
 * Importa los **clientes**,
 *
 * Construye la **factoría con volcado a memoria**. Las respuestas HTTP por defecto son un flujo de
 * red unidireccional de un solo uso [InputStream]. Si se lee el flujo para registrarlo en el log se
 * consumen los datos, el flujo se cierra y Spring ya no puede volver a leerlo para, por ejemplo,
 * convertirlo en un objeto JSON.
 *
 * Inyecta **resolvers**, que se ejecutan al resolver argumentos de los métodos de los clientes, por
 * ejemplo, para procesar anotaciones personalizadas o para alterar/convertir sus valores.
 *
 * Inyecta **procesadores**, que se ejecutan justo antes de llamar a los métodos de los clientes y
 * dan acceso reflexivo al método y al builder de la solicitud.
 *
 * Inyecta **interceptores**, que se ejecutan justo después de los procesadores, cuando ya se han
 * recopilado los datos de la solicitud, pero aún no se ha enviado al servicio remoto.
 *
 * @param argumentResolvers Resolutores de argumentos.
 * @param serviceInterceptors Interceptores de solicitudes salientes.
 * @param serviceProcessors Procesadores de solicitudes salientes.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Configuration
@EnableConfigurationProperties(ClientProfile.Properties::class)
class ClientConfigurer(
  private val argumentResolvers: List<HttpServiceArgumentResolver>,
  private val serviceInterceptors: List<ClientHttpRequestInterceptor>,
  private val serviceProcessors: List<HttpRequestValues.Processor>,
) : RestClientHttpServiceGroupConfigurer {
  /** Logger */
  private val logger = KotlinLogging.logger {}

  /**
   * Configura los grupos de clientes.
   *
   * @param groups Grupos de clientes.
   */
  override fun configureGroups(groups: HttpServiceGroupConfigurer.Groups<RestClient.Builder>) {
    groups.forEachGroup { _, clientBuilder, factoryBuilder ->

      // Configuración del builder del cliente interno que realiza las solicitudes
      clientBuilder
        .requestFactory(
          // Permite la lectura múltiple del flujo de respuesta,
          // útil para logging o interceptores
          BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()),
        ).requestInterceptors { list ->

          // Registro de interceptores
          serviceInterceptors.forEach { interceptor ->
            list.add(interceptor)
            logger.debug { "Interceptor: ${interceptor.javaClass.simpleName}" }
          }
        }

      // Configuración de la factoría de clientes

      // Registro de resolvers de argumentos
      argumentResolvers.forEach { resolver ->
        factoryBuilder.customArgumentResolver(resolver)
        logger.debug { "ArgumentResolver: ${resolver.javaClass.simpleName}" }
      }

      // Registro de procesadores
      serviceProcessors.forEach { processor ->
        factoryBuilder.httpRequestValuesProcessor(processor)
        logger.debug { "Processor: ${processor.javaClass.simpleName}" }
      }
    }
  }
}
