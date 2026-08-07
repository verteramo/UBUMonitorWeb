import { HttpClient, HttpContext, HttpContextToken } from '@angular/common/http';
import { inject, Service, Signal, signal } from '@angular/core';
import { ENDPOINTS } from '@core/api/endpoints';
import { HOST_KEY } from '@core/interceptors/host-interceptor';
import { Principal } from '@core/models/principal';
import { SESSION_STORAGE } from '@core/services/storage.service';
import { ignoreElements, Observable, tap } from 'rxjs';

// https://angular.dev/api/common/http/HttpContext
export const AUTH_SERVICE = new HttpContextToken(() => false);

const PRINCIPAL_KEY = 'principal';

interface Credentials {
  username: string;
  password: string;
}

@Service()
export class AuthService {
  private http = inject(HttpClient);
  private storage = inject(SESSION_STORAGE);

  private $principal = signal<Principal | null>(this.storage.get(PRINCIPAL_KEY));

  getPrincipal(): Signal<Principal | null> {
    return this.$principal.asReadonly();
  }

  login(host: string, credentials: Credentials): Observable<Principal> {
    this.storage.set(HOST_KEY, host);

    return this.http
      .post<Principal>(ENDPOINTS.auth.login, credentials, {
        context: new HttpContext().set(AUTH_SERVICE, true),
      })
      .pipe(
        tap((principal) => {
          this.$principal.set(principal);
          this.storage.set(PRINCIPAL_KEY, principal);
        }),
      );
  }

  logout(): Observable<never> {
    return this.http.post<void>(ENDPOINTS.auth.logout, null).pipe(
      tap(() => {
        this.$principal.set(null);
        this.storage.remove(PRINCIPAL_KEY);
      }),
      ignoreElements(),
    );
  }
}
