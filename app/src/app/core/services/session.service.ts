/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User } from '@core/models/user';
import { UserPreferences } from '@core/models/user-preferences';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/**
 * Servicio para la gestión del estado y preferencias del usuario en la sesión activa.
 */
@Injectable({ providedIn: 'root' })
export class SessionService {
  private http = inject(HttpClient);

  /**
   * Recupera los datos del usuario autenticado en la sesión actual.
   * (Útil para rehidratar el store si el usuario recarga la página, F5).
   *
   * @returns Observable con la información del usuario activo.
   */
  getUser(): Observable<User> {
    return this.http.get<User>(env.endpoints.session.user);
  }

  /**
   * Sincroniza las preferencias del usuario activo con el backend.
   *
   * @param preferences Objeto clave-valor con las preferencias actuales.
   * @returns Observable que se completa al finalizar la persistencia.
   */
  putPreferences(preferences: UserPreferences): Observable<void> {
    return this.http.put<void>(env.endpoints.session.preferences, preferences);
  }
}
