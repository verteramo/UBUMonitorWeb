import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Principal } from '@core/models/principal';
import { endpoints } from '@core/services/endpoints';
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

/**
 * Servicio de autenticación.
 *
 * @see https://angular.dev/api/common/http/HttpContext
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class AuthService {
  private http = inject(HttpClient);
  private context = new HttpContext().set(AuthRequestToken, true);

  /** Realiza el inicio de sesión. */
  login({ host, credentials }: LoginParams): Observable<Principal> {
    return this.http.post<Principal>(endpoints.login, credentials, {
      context: this.context.set(HostToken, host),
    });
  }

  /** Cierra la sesión en el servidor */
  logout() {
    return this.http.get(endpoints.logout, { context: this.context });
  }
}
