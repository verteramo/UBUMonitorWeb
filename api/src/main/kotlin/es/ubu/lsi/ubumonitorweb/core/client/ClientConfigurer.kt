/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.interceptor.ExceptionInterceptor
import es.ubu.lsi.ubumonitorweb.core.interceptor.LoggingInterceptor
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpArray
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpCollection
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpMap
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
import kotlin.time.toJavaDuration

/**
 * Configurador de clientes [HttpExchange].
 *
 * Construye la **factoría con volcado a memoria**. Las respuestas HTTP por defecto son un flujo de
 * red unidireccional de un solo uso [InputStream]. Si se lee el flujo para registrarlo en el log se
 * consumen los datos, el flujo se cierra y Spring ya no puede volver a leerlo para, por ejemplo,
 * convertirlo en un objeto JSON.
 *
 * Inyecta **resolvers**, que se ejecutan al resolver argumentos de los métodos de los clientes, por
 * ejemplo, para procesar anotaciones personalizadas o para alterar/convertir sus valores, algunas
 * anotaciones que se incluyen son: [PhpArray], [PhpCollection] o [PhpMap] y sus resolvers.
 *
 * Inyecta **procesadores**, que se ejecutan justo antes de llamar a los métodos de los clientes y
 * dan acceso reflexivo al método y al builder de la solicitud, el más importante en esta aplicación es
 * [ClientProcessor], que asume el trabajo de componer la solicitud saliente.
 *
 * Inyecta **interceptores**, que sse encuentra en la última capa en la arquitectura "onion", es decir,
 * son los últimos en tocar la solicitud a su salida y los primeros en tocar la respuesta recibida,
 * en esta arquitectura existen dos muy importantes:
 * - [LoggingInterceptor]: su única responsabilidad es loguear las solicitudes y las respuestas, se habilita
 * únicamente si la aplicación se ejecuta con el perfil `dev`: `--spring.profiles.active=dev`.
 * - [ExceptionInterceptor]: se sabe que Moodle devuelve los errores con código de estado `200 OK`, por lo que
 * se debe inferir si se trata de un error a partir del cuerpo de la solicitud, esta responsabilidad recae
 * sobre este interceptor.
 */
@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class ClientConfigurer(
  private val properties: ClientProperties,
  private val argumentResolvers: List<HttpServiceArgumentResolver>,
  private val clientProcessors: List<HttpRequestValues.Processor>,
  private val clientInterceptors: List<ClientHttpRequestInterceptor>,
) : RestClientHttpServiceGroupConfigurer {
  private val logger = KotlinLogging.logger {}

  /**
   * Configura los grupos de clientes.
   */
  override fun configureGroups(groups: HttpServiceGroupConfigurer.Groups<RestClient.Builder>) {
    groups.forEachGroup { _, clientBuilder, factoryBuilder ->
      val factory = SimpleClientHttpRequestFactory()
      factory.setChunkSize(properties.chunkSize)
      factory.setReadTimeout(properties.readTimeout)
      factory.setConnectTimeout(properties.connectionTimeout)

      clientBuilder.requestFactory(BufferingClientHttpRequestFactory(factory)).requestInterceptors { list ->
        clientInterceptors.forEach { interceptor ->
          list.add(interceptor)
          logger.debug { "Interceptor: ${interceptor.javaClass.simpleName}" }
        }
      }

      argumentResolvers.forEach { resolver ->
        factoryBuilder.customArgumentResolver(resolver)
        logger.debug { "ArgumentResolver: ${resolver.javaClass.simpleName}" }
      }

      clientProcessors.forEach { processor ->
        factoryBuilder.httpRequestValuesProcessor(processor)
        logger.debug { "Processor: ${processor.javaClass.simpleName}" }
      }
    }
  }
}
