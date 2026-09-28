/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.cookie.BasicCookieStore
import org.apache.hc.client5.http.cookie.CookieStore
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.Timeout
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver

/**
 * Configuración global de clientes HTTP.
 */
@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class ClientConfiguration {
  /**
   * Almacén en memoria que sincroniza automáticamente las cookies entre el motor HTTP y los clientes de la aplicación.
   *
   * @return Almacén de cookies.
   */
  @Bean
  fun cookieStore(): CookieStore = BasicCookieStore()

  /**
   * Construye la factoría de clientes HTTP vinculando el almacén global de cookies.
   *
   * @return Factoría de clientes HTTP.
   */
  @Bean
  fun clientFactory(
    properties: ClientProperties,
    cookieStore: CookieStore,
  ): ClientHttpRequestFactory {
    val connectionConfig =
      ConnectionConfig.custom().setConnectTimeout(Timeout.of(properties.factory.connectionTimeout)).build()

    val connectionManager =
      PoolingHttpClientConnectionManagerBuilder.create().setDefaultConnectionConfig(connectionConfig).build()

    val httpClient =
      HttpClients
        .custom()
        .setConnectionManager(connectionManager)
        .setDefaultCookieStore(cookieStore)
        .build()

    val requestFactory =
      HttpComponentsClientHttpRequestFactory(httpClient).apply {
        setReadTimeout(properties.factory.readTimeout)
        setConnectionRequestTimeout(properties.factory.connectionTimeout)
      }

    return BufferingClientHttpRequestFactory(requestFactory)
  }

  /**
   * Incorpora los componentes de apoyo a los clientes HTTP en la cadena de ejecución.
   *
   * @param factory Factoría de clientes HTTP.
   * @param resolvers Resolvers de argumentos.
   * @param processors Procesadores de solicitudes.
   * @param interceptors Interceptores de solicitudes y respuestas.
   */
  @Bean
  fun clientGroupConfigurer(
    factory: ClientHttpRequestFactory,
    resolvers: List<HttpServiceArgumentResolver>,
    processors: List<HttpRequestValues.Processor>,
    interceptors: List<ClientHttpRequestInterceptor>,
  ) = RestClientHttpServiceGroupConfigurer { groups ->
    groups.forEachGroup { _, clientBuilder, factoryBuilder ->
      clientBuilder.requestFactory(factory).requestInterceptors { it.addAll(interceptors) }
      resolvers.forEach(factoryBuilder::customArgumentResolver)
      processors.forEach(factoryBuilder::httpRequestValuesProcessor)
    }
  }
}
