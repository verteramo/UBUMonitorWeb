import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Router } from '@angular/router';
import { endpoints } from '@core/api/endpoints';
import { CourseStore } from '@core/store/course.store';
import { Principal, PrincipalStore } from '@core/store/principal.store';
import { catchError, EMPTY, Observable, tap } from 'rxjs';

/**
 * Token para etiquetar solicitudes de login.
 */
export const LOGIN_REQUEST_TOKEN = new HttpContextToken(() => false);

/**
 * Token para añadir el host en solicitudes de login.
 *
 * @see https://angular.dev/api/common/http/HttpContext
 */
export const HOST_TOKEN = new HttpContextToken(() => '');

/**
 * Contrato para credenciales de autenticación.
 */
interface Credentials {
  username: string;
  password: string;
}

/**
 * Servicio de autenticación.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private principalStore = inject(PrincipalStore);
  private courseStore = inject(CourseStore);

  /**
   * Realiza el inicio de sesión y guarda el Principal en su store.
   *
   * @param host Servidor Moodle.
   * @param credentials Credenciales de autenticación.
   * @returns Observable con el objeto Principal.
   */
  login(host: string, credentials: Credentials): Observable<Principal> {
    return this.http
      .post<Principal>(endpoints.login, credentials, {
        context: new HttpContext().set(HOST_TOKEN, host).set(LOGIN_REQUEST_TOKEN, true),
      })
      .pipe(tap((principal) => this.principalStore.set(principal)));
  }

  /**
   * Limpia los stores del Principal y el Course, redirige al
   * login y cierra la sesión en el servidor en segundo plano.
   */
  logout() {
    this.principalStore.clear();
    this.courseStore.clear();

    this.router.navigate(['/login']);

    this.http
      .get(endpoints.logout)
      .pipe(catchError(() => EMPTY))
      .subscribe();
  }
}
