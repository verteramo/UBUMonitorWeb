package es.ubu.lsi.ubumonitorweb.core.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.GlobalOpenApiCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OpenApiProperties::class)
class OpenApiConfiguration(private val properties: OpenApiProperties) {

  @Bean
  fun globalOpenApiCustomizer(): GlobalOpenApiCustomizer {
    return GlobalOpenApiCustomizer { openAPI ->
      openAPI.paths?.values?.forEach { path ->
        path.readOperations().forEach { operation ->
          properties.parameters.forEach { (name, props) ->
            operation.addParametersItem(props.parameter.name(name))
          }
        }
      }
    }
  }

  @Bean
  fun customOpenApi(): OpenAPI {
    val openAPI = OpenAPI()
    val components = Components()

    properties.securitySchemes.forEach { (key, props) ->
      components.addSecuritySchemes(key, props.securityScheme)
      openAPI.addSecurityItem(SecurityRequirement().addList(key))
    }

    return openAPI.components(components)
  }
}
