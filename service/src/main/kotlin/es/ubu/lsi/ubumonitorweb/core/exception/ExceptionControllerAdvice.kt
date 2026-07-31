package es.ubu.lsi.ubumonitorweb.core.exception

import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice {
  @ExceptionHandler(MoodleException::class)
  fun moodleException(e: MoodleException) =
    ProblemDetail.forStatusAndDetail(
      e.status,
      e.message,
    )
}
