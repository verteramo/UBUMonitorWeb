package es.ubu.lsi.ubumonitorweb.core.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.GlobalOpenApiCustomizer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.full.createInstance

@Configuration
class OpenApiConfig(private val properties: Properties) {

  @ConfigurationProperties("openapi")
  data class Properties(
      val parameters: Map<String, ParameterProperties> = emptyMap(),
      val securitySchemes: Map<String, SecuritySchemeProperties> = emptyMap(),
  ) {
    /**
     * Propiedades de un parámetro.
     *
     * https://github.com/OAI/OpenAPI-Specification/blob/3.0.4/versions/3.0.4.md#parameter-object
     */
    data class ParameterProperties(
        val `in`: In = In.HEADER,
        val required: Boolean = true,
        val description: String = "",
        val schema: Class<out Schema<*>> = StringSchema::class.java,
    ) {
      enum class In { HEADER, COOKIE, QUERY, PATH }

      val parameter: Parameter
        get() {
          return Parameter()
              .`in`(`in`.name.lowercase())
              .description(description)
              .required(required)
              .schema(schema.kotlin.createInstance())
        }
    }

    /**
     * Propiedades de un esquema de seguridad.
     *
     * https://github.com/OAI/OpenAPI-Specification/blob/3.0.4/versions/3.0.4.md#security-scheme-object
     */
    data class SecuritySchemeProperties(
        val type: SecurityScheme.Type,
        val `in`: SecurityScheme.In,
        val name: String,
        val description: String = "",
        /**
         * https://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml
         */
        val scheme: String = "",
        val bearerFormat: String = "",
    ) {
      val securityScheme: SecurityScheme
        get() {
          return SecurityScheme()
              .type(type)
              .`in`(`in`)
              .name(name)
              .description(description)
              .scheme(scheme)
              .bearerFormat(bearerFormat)
        }
    }
  }

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
