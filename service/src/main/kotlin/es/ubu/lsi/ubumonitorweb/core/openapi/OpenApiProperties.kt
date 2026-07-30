package es.ubu.lsi.ubumonitorweb.core.openapi

import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.reflect.full.createInstance

@ConfigurationProperties("openapi")
data class OpenApiProperties(
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
