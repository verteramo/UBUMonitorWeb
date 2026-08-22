import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { User } from '@core/models/user';
import { Observable } from 'rxjs';
import { endpoints } from './endpoints';

/**
 * Servicio de usuarios.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class UserService {
  private http = inject(HttpClient);

  getUsers(courseId: number): Observable<User[]> {
    return this.http.get<User[]>(endpoints.users, { params: { courseId } });
  }
}
