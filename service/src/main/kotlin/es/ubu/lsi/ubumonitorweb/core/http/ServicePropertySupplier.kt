package es.ubu.lsi.ubumonitorweb.core.http

fun interface ServiceParamSupplier<out T : Any> {

  fun ServiceContext.get(): T
}
