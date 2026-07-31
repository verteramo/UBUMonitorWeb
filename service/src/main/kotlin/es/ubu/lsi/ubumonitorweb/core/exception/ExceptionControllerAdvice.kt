package es.ubu.lsi.ubumonitorweb.core.exception

import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ExceptionControllerAdvice {

  @ExceptionHandler(ResponseStatusException::class)
  fun responseStatusException(e: ResponseStatusException): ProblemDetail {
    return e.body
  }
}
