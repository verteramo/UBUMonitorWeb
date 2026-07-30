package es.ubu.lsi.ubumonitorweb.core.service

import org.springframework.core.MethodParameter
import java.lang.reflect.Method

fun interface ServiceParamSupplier<T : Any> : (Method, Map<MethodParameter, Any?>) -> T
