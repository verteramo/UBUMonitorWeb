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
export class SystemPreferencesService {
  private readonly http = inject(HttpClient);

  /**
   * Lee las preferencias actuales del sistema.
   *
   * @returns Observable con el objeto de preferencias deserializado.
   */
  read(): Observable<SystemPreferences> {
    return this.http.get<SystemPreferences>(env.endpoints.preferences);
  }

  /**
   * Sobrescribe y persiste el estado completo de las preferencias.
   *
   * @param preferences Objeto completo con las preferencias a guardar.
   */
  write(preferences: SystemPreferences): Observable<void> {
    return this.http.put<void>(env.endpoints.preferences, preferences);
  }
}
