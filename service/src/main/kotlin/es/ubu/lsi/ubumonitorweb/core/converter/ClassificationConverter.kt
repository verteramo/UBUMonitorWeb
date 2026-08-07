package es.ubu.lsi.ubumonitorweb.core.converter

import es.ubu.lsi.ubumonitorweb.feature.controller.Classification
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ClassificationConverter : Converter<String, Classification> {
  override fun convert(source: String) = Classification.valueOf(source.uppercase())
}
