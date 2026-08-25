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

/** Token para etiquetar solicitudes del AuthService. */
export const AuthRequestToken = new HttpContextToken(() => false);

/** Token para añadir el host en solicitudes de login. */
export const HostToken = new HttpContextToken(() => '');

/** Servicio de autenticación. */
@Service()
export class AuthService {
  #http = inject(HttpClient);
  #context = new HttpContext().set(AuthRequestToken, true);

  /** Inicio de sesión en el servicio. */
  login({ host, credentials }: LoginParams): Observable<Principal> {
    return this.#http.post<Principal>(env.endpoints.login, credentials, {
      context: this.#context.set(HostToken, host),
    });
  }

  /** Cierre de sesión en el servicio. */
  logout() {
    this.#http.get(env.endpoints.logout, { context: this.#context });
  }
}
