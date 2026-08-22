import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Router } from '@angular/router';
import { endpoints } from '@core/api/endpoints';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
import { catchError, EMPTY, Observable, tap } from 'rxjs';

/**
 * Token para etiquetar solicitudes de login.
 */
export const LoginRequestToken = new HttpContextToken(() => false);

/**
 * Token para añadir el host en solicitudes de login.
 *
 * @see https://angular.dev/api/common/http/HttpContext
 */
export const HostToken = new HttpContextToken(() => '');

/**
 * Servicio de autenticación.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private sessionStore = inject(SessionStore);

  /**
   * Realiza el inicio de sesión y guarda el Principal en su store.
   *
   * @param host Servidor Moodle.
   * @param credentials Credenciales de autenticación.
   * @returns Observable con el objeto Principal.
   */
  login(host: string, credentials: { username: string; password: string }): Observable<Principal> {
    return this.http
      .post<Principal>(endpoints.login, credentials, {
        context: new HttpContext().set(HostToken, host).set(LoginRequestToken, true),
      })
      .pipe(tap((principal) => this.sessionStore.setPrincipal(principal)));
  }

  /**
   * Limpia los stores del Principal y el Course, redirige al
   * login y cierra la sesión en el servidor en segundo plano.
   */
  logout() {
    this.sessionStore.clearSession();

    this.router.navigate(['/login']);

    this.http
      .get(endpoints.logout)
      .pipe(catchError(() => EMPTY))
      .subscribe();
  }
}
