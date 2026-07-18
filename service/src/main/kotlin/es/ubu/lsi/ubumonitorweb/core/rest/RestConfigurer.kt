package es.ubu.lsi.ubumonitorweb.core.rest

import es.ubu.lsi.ubumonitorweb.Application
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.registry.HttpServiceGroupConfigurer
import org.springframework.web.service.registry.ImportHttpServices
import java.io.InputStream


/**
 * Configurador de servicios [HttpExchange].
 *
 * Importa los **servicios**,
 *
 * Construye la **factoría con volcado a memoria**. Las respuestas HTTP por
 * defecto son un flujo de red unidireccional de un solo uso [InputStream]. Si
 * se lee el flujo para registrarlo en el log se consumen los datos, el flujo se
 * cierra y Spring ya no puede volver a leerlo para, por ejemplo, convertirlo en
 * un objeto JSON.
 *
 * Inyecta **procesadores**, que se ejecutan justo antes de llamar a los métodos
 * de los servicios y dan acceso reflexivo al método y al builder de la
 * solicitud.
 *
 * Inyecta **interceptores**, que se ejecutan justo después de los procesadores,
 * cuando ya se han recopilado los datos de la solicitud, pero aún no se ha
 * enviado al servicio remoto.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Configuration
@ImportHttpServices(basePackageClasses = [Application::class])
class RestConfigurer(
    private val messageConverters: List<HttpMessageConverter<*>>,
    private val serviceInterceptors: List<ClientHttpRequestInterceptor>,
    private val serviceProcessors: List<HttpRequestValues.Processor>,
) : RestClientHttpServiceGroupConfigurer {

  private val logger = KotlinLogging.logger {}

  override fun configureGroups(groups: HttpServiceGroupConfigurer.Groups<RestClient.Builder>) {
    logger.debug { "MessageConverters count: ${messageConverters.size}" }
    logger.debug { "ServiceInterceptors count: ${serviceInterceptors.size}" }
    logger.debug { "ServiceProcessors count: ${serviceProcessors.size}" }

    groups.forEachGroup { _, clientBuilder, factoryBuilder ->

      clientBuilder
          .requestFactory(
            // Permite la lectura múltiple del flujo de respuesta,
            // útil para logging o interceptores
            BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()),
          )
          .configureMessageConverters { builder ->
            builder.registerDefaults()
            messageConverters.forEach { converter ->
              builder.addCustomConverter(converter)
            }
          }
          .requestInterceptors { list ->
            serviceInterceptors.forEach { interceptor ->
              list.add(interceptor)
            }
          }

      serviceProcessors.forEach { processor ->
        factoryBuilder.httpRequestValuesProcessor(processor)
      }
    }
  }
}
