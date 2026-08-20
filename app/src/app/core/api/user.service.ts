import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { User } from '@core/models/user';
import { map, Observable } from 'rxjs';
import { endpoints } from './endpoints';

interface UserDTO extends Omit<User, 'firstaccess' | 'lastaccess' | 'lastcourseaccess'> {
  firstaccess?: number;
  lastaccess?: number;
  lastcourseaccess?: number;
}

/**
 * Servicio de usuarios.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class UserService {
  private http = inject(HttpClient);

  getUsers(courseId: number): Observable<User[]> {
    return this.http.get<UserDTO[]>(endpoints.users, { params: { courseId } }).pipe(
      map((users) =>
        users.map((user) => ({
          ...user,
          firstaccess: user.firstaccess ? new Date(user.firstaccess * 1000) : undefined,
          lastaccess: user.lastaccess ? new Date(user.lastaccess * 1000) : undefined,
          lastcourseaccess: user.lastcourseaccess
            ? new Date(user.lastcourseaccess * 1000)
            : undefined,
        })),
      ),
    );
  }
}
