/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { LoginOptions } from '@core/models/login-options';
import { User } from '@core/models/user';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/**
 * Parámetros de inicio de sesión para el método `login`.
 */
export type CredentialsLoginRequest = {
  host: string;
  payload: {
    username: string;
    password: string;
  };
};

export type SsoTokenLoginRequest = {
  host: string;
  payload: {
    token: string;
    privateToken?: string;
  };
};

/**
 * Token para etiquetar solicitudes de este `AuthService`.
 */
export const AuthToken = new HttpContextToken<string | null>(() => null);

/**
 * Servicio de autenticación.
 */
@Service()
export class AuthService {
  private http = inject(HttpClient);

  /**
   * Toque al endpoint de descubrimiento para obtener los detalles del login.
   */
  discover(host: string): Observable<LoginOptions> {
    return this.http.get<LoginOptions>(env.endpoints.discover, {
      context: new HttpContext().set(AuthToken, host),
    });
  }

  /**
   * Inicio de sesión en el backend.
   */
  login({ host, payload }: CredentialsLoginRequest): Observable<User> {
    return this.http.post<User>(env.endpoints.login, payload, {
      context: new HttpContext().set(AuthToken, host),
    });
  }

  loginSso({ host, payload }: SsoTokenLoginRequest): Observable<User> {
    return this.http.post<User>(env.endpoints.loginSso, payload, {
      context: new HttpContext().set(AuthToken, host),
    });
  }

  /**
   * Cierre de sesión en el backend.
   */
  logout(): Observable<void> {
    return this.http.get<void>(env.endpoints.logout);
  }
}
