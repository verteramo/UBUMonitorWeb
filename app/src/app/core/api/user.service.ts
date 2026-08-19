import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { MoodleUser, User } from '@core/models/user';
import { map, Observable } from 'rxjs';
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
    return this.http.get<MoodleUser[]>(endpoints.users, {
      params: { courseId }
    }).pipe(
      map((users) =>
        users.map((user) => ({
          ...user,
firstaccess:
            user.firstaccess != null && user.firstaccess !== 0
              ? new Date(user.firstaccess * 1000)
              : undefined,
          lastaccess:
            user.lastaccess != null && user.lastaccess !== 0
              ? new Date(user.lastaccess * 1000)
              : undefined,
          lastcourseaccess:
            user.lastcourseaccess != null && user.lastcourseaccess !== 0
              ? new Date(user.lastcourseaccess * 1000)
              : undefined,
        })),
      ),
    );
  }
}
