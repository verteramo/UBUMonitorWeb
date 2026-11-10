package es.ubu.lsi.ubumonitorweb.core.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class SessionConflictException(
  message: String,
) : AuthenticationException(message)
