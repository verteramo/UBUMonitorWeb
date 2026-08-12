package es.ubu.lsi.ubumonitorweb.core.exception

import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * ControllerAdvice capturador de excepciones de tipo [MoodleException].
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@RestControllerAdvice
class ExceptionControllerAdvice {
  /**
   * Captura y convierte un MoodleException en un error normalizado.
   *
   * @param e Excepción de tipo [MoodleException].
   * @return Objeto de tipo [ProblemDetail] normalizado.
   */
  @ExceptionHandler(MoodleException::class)
  fun moodleException(e: MoodleException) = ProblemDetail.forStatusAndDetail(e.status, e.message)
}
