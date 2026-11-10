package es.ubu.lsi.ubumonitorweb.core.security

import org.apache.hc.client5.http.cookie.Cookie
import org.apache.hc.client5.http.cookie.CookieStore
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie
import org.springframework.stereotype.Service

@Service
class CookieService(
  private val cookieStore: CookieStore,
) {
  fun addCookie(vararg cookies: UserEntity.Vault.Cookie) {
    cookieStore.clear()
    cookies.forEach { cookie ->
      cookieStore.addCookie(
        cookie.run {
          BasicClientCookie(name, value).also {
            it.domain = domain
            it.path = path
            it.isSecure = isSecure

            it.setAttribute(Cookie.DOMAIN_ATTR, domain)
            it.setAttribute(Cookie.PATH_ATTR, path)

            if (isSecure) {
              it.setAttribute(Cookie.SECURE_ATTR, "true")
            }
          }
        },
      )
    }
  }

  fun getCookie(namePrefix: String): UserEntity.Vault.Cookie? =
    cookieStore.cookies.firstOrNull { it.name.startsWith(namePrefix) }?.let {
      UserEntity.Vault.Cookie(
        name = it.name,
        path = it.path,
        value = it.value,
        domain = it.domain,
        isSecure = it.isSecure,
      )
    }

  fun clearStore() = cookieStore.clear()
}
