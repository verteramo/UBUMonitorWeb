import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { SystemPreferences } from '@core/models/system-preferences';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/**
 * Servicio encargado de la comunicación HTTP con el backend
 * para la persistencia y lectura de las preferencias del sistema.
 */
@Service()
export class SystemService {
  private readonly http = inject(HttpClient);

  /**
   * Lee las preferencias actuales del sistema.
   *
   * @returns Observable con el objeto de preferencias deserializado.
   */
  getPreferences(): Observable<SystemPreferences> {
    return this.http.get<SystemPreferences>(env.endpoints.system.preferences);
  }

  /**
   * Sobrescribe y persiste el estado completo de las preferencias.
   *
   * @param preferences Objeto completo con las preferencias a guardar.
   */
  putPreferences(preferences: SystemPreferences): Observable<void> {
    return this.http.put<void>(env.endpoints.system.preferences, preferences);
  }
}
