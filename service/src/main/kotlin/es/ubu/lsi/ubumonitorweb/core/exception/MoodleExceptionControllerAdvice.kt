package es.ubu.lsi.ubumonitorweb.core.exception

import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class MoodleExceptionControllerAdvice {
  @ExceptionHandler(MoodleException::class)
  fun handler(e: MoodleException) = ProblemDetail.forStatusAndDetail(e.status, e.message)
}
