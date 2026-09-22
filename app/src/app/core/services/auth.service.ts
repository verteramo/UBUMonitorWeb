/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { LoginOptions } from '@core/models/login-options';
import { Principal } from '@core/models/principal';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/**
 * Parámetros de inicio de sesión para el método `login`.
 */
export type LoginParams = {
  host: string;
  credentials: {
    username: string;
    password: string;
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
  login({ host, credentials }: LoginParams): Observable<Principal> {
    return this.http.post<Principal>(env.endpoints.login, credentials, {
      context: new HttpContext().set(AuthToken, host),
    });
  }

  /**
   * Cierre de sesión en el backend.
   */
  logout(): void {
    this.http.get(env.endpoints.logout);
  }
}
