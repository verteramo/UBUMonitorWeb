import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Router } from '@angular/router';
import { ENDPOINTS } from '@core/api/endpoints';
import { Principal } from '@core/models/principal';
import { SESSION_STORAGE } from '@core/services/storage.service';
import { CourseStore } from '@core/store/course.store';
import { HostStore } from '@core/store/host.store';
import { PrincipalStore } from '@core/store/principal.store';
import { catchError, EMPTY, Observable, tap } from 'rxjs';

// https://angular.dev/api/common/http/HttpContext
export const AUTH_SERVICE = new HttpContextToken(() => false);
export const HOST_TOKEN = new HttpContextToken(() => '');

interface Credentials {
  username: string;
  password: string;
}

@Service()
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private storage = inject(SESSION_STORAGE);
  private hostStore = inject(HostStore);
  private principalStore = inject(PrincipalStore);
  private courseStore = inject(CourseStore);

  login(host: string, credentials: Credentials): Observable<Principal> {
    this.hostStore.set(host);

    return this.http
      .post<Principal>(ENDPOINTS.auth.login, credentials, {
        context: new HttpContext().set(AUTH_SERVICE, true),
      })
      .pipe(tap((principal) => this.principalStore.set(principal)));
  }

  logout() {
    this.hostStore.clear();
    this.principalStore.clear();
    this.courseStore.clear();

    this.router.navigate(['/login']);

    this.http
      .get(ENDPOINTS.auth.logout, {
        context: new HttpContext().set(AUTH_SERVICE, true),
      })
      .pipe(catchError(() => EMPTY))
      .subscribe();
  }
}
