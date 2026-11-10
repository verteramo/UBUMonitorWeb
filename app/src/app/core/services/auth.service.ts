/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { SiteConfig } from '@core/models/site-config';
import { User } from '@core/models/user';
import { UserPreferences } from '@core/models/user-preferences';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/**
 * Parámetros de inicio de sesión mediante credenciales.
 */
export type CredentialsLoginRequest = {
  site: string;
  payload: {
    username: string;
    password: string;
  };
};

/**
 * Parámetros de inicio de sesión mediante token SSO.
 */
export type SsoTokenLoginRequest = {
  site: string;
  payload: {
    token: string;
  };
};

/**
 * Respuesta unificada del backend al iniciar sesión.
 */
export type LoginResponse = {
  user: User;
  preferences: UserPreferences;
};

/**
 * Token para etiquetar solicitudes de este servicio.
 */
export const AuthToken = new HttpContextToken<string | null>(() => null);

/**
 * Servicio de autenticación.
 */
@Service()
export class AuthService {
  private http = inject(HttpClient);

  /**
   * Toque al endpoint de descubrimiento para obtener la configuración del sitio.
   *
   * @param site URL del sitio Moodle.
   * @returns Observable con las opciones de inicio de sesión disponibles.
   */
  discover(site: string): Observable<SiteConfig> {
    return this.http.get<SiteConfig>(env.endpoints.auth.discover, {
      context: new HttpContext().set(AuthToken, site),
    });
  }

  /**
   * Inicio de sesión nediante credenciales.
   *
   * @param request Objeto que contiene el sitio y las credenciales.
   * @returns Observable con la información y preferencias del usuario autenticado.
   */
  login({ site, payload }: CredentialsLoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(env.endpoints.auth.login, payload, {
      context: new HttpContext().set(AuthToken, site),
    });
  }

  /**
   * Inicio de sesión mediante token SSO.
   *
   * @param request Objeto que contiene el sitio y los tokens del webservice.
   * @returns Observable con la información y preferencias del usuario autenticado.
   */
  loginSso({ site, payload }: SsoTokenLoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(env.endpoints.auth.loginSso, payload, {
      context: new HttpContext().set(AuthToken, site),
    });
  }

  /**
   * Cierre de sesión en el backend.
   *
   * @returns Observable que se completa al finalizar el cierre de sesión.
   */
  logout(): Observable<void> {
    return this.http.get<void>(env.endpoints.auth.logout);
  }
}
