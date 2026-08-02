import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { MoodlePrincipal } from '../models/moodle-principal';
import { ENDPOINTS } from '../constants/endpoints';
import { ProblemDetail } from '../models/problem-detail';

@Service()
export class AuthService {
  private http = inject(HttpClient);

  login(username: string, password: string): Observable<MoodlePrincipal> {
    const body = new HttpParams().set('username', username).set('password', password);

    return this.http.post<MoodlePrincipal>(ENDPOINTS.auth.login, body).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(() => error.error as ProblemDetail);
      }),
    );
  }
}
