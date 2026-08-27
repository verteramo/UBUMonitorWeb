package es.ubu.lsi.ubumonitorweb.core.moodle

import tools.jackson.databind.util.StdConverter

class ResourceUrlConverter : StdConverter<String, String>() {
  private val pattern = Regex("""/(\d+)/user/icon/[^/]+/([^/?]+)""")

  override fun convert(value: String?): String? =
    value?.takeIf { it.isNotBlank() }?.let {
      pattern.find(it)?.destructured?.let { (id, size) ->
        "/api/users/icon/$id/$size"
      }
    }
}
