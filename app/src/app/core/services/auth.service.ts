/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Principal } from '@core/models/principal';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/** Parámetros de inicio de sesión. */
export type LoginParams = {
  host: string;
  credentials: {
    username: string;
    password: string;
  };
};

/** Token para etiquetar solicitudes de este AuthService. */
export const AuthToken = new HttpContextToken<string | null>(() => null);

/** Servicio de autenticación. */
@Service()
export class AuthService {
  private http = inject(HttpClient);

  /** Inicio de sesión en el backend. */
  login({ host, credentials }: LoginParams): Observable<Principal> {
    return this.http.post<Principal>(env.endpoints.login, credentials, {
      context: new HttpContext().set(AuthToken, host),
    });
  }

  /** Cierre de sesión en el backend. */
  logout(): void {
    this.http.get(env.endpoints.logout);
  }
}
