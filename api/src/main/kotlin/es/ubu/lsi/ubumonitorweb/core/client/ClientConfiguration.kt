/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.cookie.BasicCookieStore
import org.apache.hc.client5.http.cookie.CookieStore
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.HttpClientConnectionManager
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.client5.http.ssl.TlsSocketStrategy
import org.apache.hc.client5.http.ssl.TrustAllStrategy
import org.apache.hc.core5.http.HttpRequestInterceptor
import org.apache.hc.core5.http.HttpResponseInterceptor
import org.apache.hc.core5.ssl.SSLContexts
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
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

/**
 * Configuración global de clientes HTTP.
 */
@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class ClientConfiguration {
  /**
   * Almacén en memoria que sincroniza automáticamente las cookies entre el motor HTTP y los clientes.
   *
   * @return Almacén de cookies.
   */
  @Bean
  fun cookieStore(): CookieStore = BasicCookieStore()

  /**
   * Construye el contexto SSL basándose en la estrategia de confianza configurada.
   *
   * @param properties Propiedades de configuración.
   * @return Instancia de [SSLContext].
   */
  @Bean
  fun sslContext(properties: ClientProperties): SSLContext =
    SSLContexts
      .custom()
      .apply {
        if (properties.factory.sslTrustStrategy == ClientProperties.FactoryProperties.SslTrustStrategy.TRUST_ALL) {
          loadTrustMaterial(TrustAllStrategy.INSTANCE)
        }
      }.build()

  /**
   * Determina el verificador de nombres de host según la estrategia de confianza configurada.
   *
   * @param properties Propiedades de configuración.
   * @return Instancia de [HostnameVerifier].
   */
  @Bean
  fun hostnameVerifier(properties: ClientProperties): HostnameVerifier =
    when (properties.factory.sslTrustStrategy) {
      ClientProperties.FactoryProperties.SslTrustStrategy.STRICT -> DefaultHostnameVerifier()
      ClientProperties.FactoryProperties.SslTrustStrategy.TRUST_ALL -> NoopHostnameVerifier.INSTANCE
    }

  /**
   * Define la estrategia TLS usando el contexto SSL y el verificador de host.
   *
   * @param sslContext Contexto SSL.
   * @param hostnameVerifier Validador de nombres de host.
   * @return Estrategia TLS.
   */
  @Bean
  fun tlsStrategy(
    sslContext: SSLContext,
    hostnameVerifier: HostnameVerifier,
  ): TlsSocketStrategy =
    ClientTlsStrategyBuilder
      .create()
      .setSslContext(sslContext)
      .setHostnameVerifier(hostnameVerifier)
      .buildClassic()

  /**
   * Configura el gestor de conexiones HTTP aplicando la estrategia TLS y los timeouts.
   *
   * @param properties Propiedades de configuración.
   * @param tlsStrategy Estrategia TLS.
   * @return Gestor de conexiones HTTP.
   */
  @Bean
  fun httpClientConnectionManager(
    properties: ClientProperties,
    tlsStrategy: TlsSocketStrategy,
  ): HttpClientConnectionManager {
    val connectionConfig =
      ConnectionConfig.custom().setConnectTimeout(Timeout.of(properties.factory.connectionTimeout)).build()

    return PoolingHttpClientConnectionManagerBuilder
      .create()
      .setTlsSocketStrategy(tlsStrategy)
      .setDefaultConnectionConfig(connectionConfig)
      .build()
  }

  /**
   * Instancia el cliente HTTP de Apache vinculando el gestor de conexiones, cookies e interceptores.
   *
   * @param connectionManager Gestor de conexiones HTTP.
   * @param cookieStore Almacén de cookies.
   * @param requestInterceptors Interceptores de peticiones de red.
   * @param responseInterceptors Interceptores de respuestas de red.
   * @return Cliente HTTP de Apache.
   */
  @Bean
  fun httpClient(
    connectionManager: HttpClientConnectionManager,
    cookieStore: CookieStore,
    requestInterceptors: Collection<HttpRequestInterceptor>,
    responseInterceptors: Collection<HttpResponseInterceptor>,
  ): CloseableHttpClient =
    HttpClients
      .custom()
      .setConnectionManager(connectionManager)
      .setDefaultCookieStore(cookieStore)
      .apply {
        requestInterceptors.forEach { addRequestInterceptorLast(it) }
        responseInterceptors.forEach { addResponseInterceptorLast(it) }
      }.build()

  /**
   * Construye la factoría de solicitudes HTTP para Spring incorporando timeouts de lectura y búfer.
   *
   * @param properties Propiedades de configuración.
   * @param httpClient Cliente HTTP de Apache.
   * @return Factoría de peticiones HTTP en búfer.
   */
  @Bean
  fun clientHttpRequestFactory(
    properties: ClientProperties,
    httpClient: CloseableHttpClient,
  ): ClientHttpRequestFactory {
    val requestFactory =
      HttpComponentsClientHttpRequestFactory(httpClient).apply {
        setReadTimeout(properties.factory.readTimeout)
        setConnectionRequestTimeout(properties.factory.connectionTimeout)
      }
    return BufferingClientHttpRequestFactory(requestFactory)
  }

  /**
   * Incorpora los componentes de apoyo y el grupo de servicios a la cadena de ejecución de Spring.
   *
   * @param factory Factoría de clientes HTTP.
   * @param resolvers Resolvers de argumentos personalizados.
   * @param processors Procesadores de valores de solicitud.
   * @param interceptors Interceptores de solicitudes del framework.
   * @return Configurador de grupos de servicios HTTP.
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
