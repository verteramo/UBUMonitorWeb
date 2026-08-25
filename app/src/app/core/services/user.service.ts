/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { User } from '@core/models/user';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/** Servicio de usuarios. */
@Service()
export class UserService {
  /** Cliente HTTP. */
  #http = inject(HttpClient);

  /**
   * Obtiene una lista de usuarios de un curso determinado.
   *
   * @param courseId ID del curso.
   * @returns Lista de usuarios del curso.
   */
  getUsers(courseId: number): Observable<User[]> {
    return this.#http.get<User[]>(env.endpoints.users, { params: { courseId } });
  }
}
