package es.ubu.lsi.ubumonitorweb.core.locale

enum class Message(
  val code: String,
) {
  ERROR_HTTP_HEADER_MISSING("error.http_header_missing"),
  ERROR_AUTH_SESSKEY_MISSING("error.auth_sesskey_missing"),
  ERROR_AUTH_COOKIE_MISSING("error.auth_cookie_missing"),
  ERROR_AUTH_TOKEN_MISSING("error.auth_token_missing"),
  ERROR_AUTH_PASSPORT_MISSING("error.auth_passport_missing"),
  ERROR_AUTH_PASSPORT_INVALID("error.auth_passport_invalid"),
  ERROR_AUTH_LOGIN_FAILED("error.auth_login_failed"),
}
