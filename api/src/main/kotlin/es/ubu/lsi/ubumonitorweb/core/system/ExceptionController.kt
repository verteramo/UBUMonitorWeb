package es.ubu.lsi.ubumonitorweb.core.system

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException

@RestControllerAdvice
class ExceptionController {
  @ExceptionHandler(HttpClientErrorException::class)
  fun handleHttpClientErrorException(exception: HttpClientErrorException): ProblemDetail =
    ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      Message.ERROR_BAD_MOODLE(),
    )
}
