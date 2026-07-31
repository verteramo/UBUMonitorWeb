package es.ubu.lsi.ubumonitorweb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationTest {

  @LocalServerPort
  private var port: Int = 0

  private val restTemplate = TestRestTemplate()

  private fun url(path: String): String = "http://localhost:$port$path"

  private fun createHeaders(): HttpHeaders = HttpHeaders().apply {
    contentType = MediaType.APPLICATION_FORM_URLENCODED
    set("Moodle-Host", "https://school.moodledemo.net/")
  }

  private fun performLogin(user: String, pass: String): ResponseEntity<Map<*, *>> {
    val requestBody = LinkedMultiValueMap<String, String>().apply {
      add("username", user)
      add("password", pass)
    }
    return restTemplate.postForEntity(
      url("/login"),
      HttpEntity(requestBody, createHeaders()),
      Map::class.java,
    )
  }

  @Test
  @DisplayName("Should authenticate and retrieve session cookie and user JSON")
  fun shouldAuthenticateSuccessfully() {
    val response = performLogin("teacher", "moodle26")

    assertEquals(HttpStatus.OK, response.statusCode)
    assertTrue(!response.headers[HttpHeaders.SET_COOKIE].isNullOrEmpty())
    val body = response.body as? Map<*, *>
    assertEquals("teacher", body?.get("username"))
  }

  @Test
  @DisplayName("Should reject invalid credentials with 401 and ProblemDetail")
  fun shouldRejectInvalidCredentials() {
    val requestBody = LinkedMultiValueMap<String, String>().apply {
      add("username", "teacher")
      add("password", "moodle")
    }

    val response = restTemplate.postForEntity(
      url("/api/login"),
      HttpEntity(requestBody, createHeaders()),
      ProblemDetail::class.java,
    )

    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    assertEquals(401, response.body?.status)
    assertEquals("Unauthorized", response.body?.title)
  }

  @Test
  @DisplayName("Should logout successfully and invalidate subsequent access")
  fun shouldLogoutSuccessfully() {
    val loginResponse = performLogin("teacher", "moodle26")
    assertEquals(HttpStatus.OK, loginResponse.statusCode)

    val cookies = loginResponse.headers[HttpHeaders.SET_COOKIE]
    val authHeaders = HttpHeaders().apply {
      cookies?.let { set(HttpHeaders.COOKIE, it.first()) }
    }

    val logoutResponse = restTemplate.postForEntity(
      url("/api/logout"),
      HttpEntity(null, authHeaders),
      Void::class.java,
    )
    assertEquals(HttpStatus.OK, logoutResponse.statusCode)

    val protectedResponse = restTemplate.exchange(
      url("/api/users"),
      HttpMethod.GET,
      HttpEntity(null, authHeaders),
      Void::class.java,
    )
    assertEquals(HttpStatus.UNAUTHORIZED, protectedResponse.statusCode)
  }
}