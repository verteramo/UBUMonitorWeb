import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AUTH_SERVICE, AuthService } from '@core/api/auth.service';
import { catchError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const authErrorStatuses = [401, 403];
  const isAuthService = req.context.get(AUTH_SERVICE) === true;

  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      if (!isAuthService && authErrorStatuses.includes(response.status)) {
        authService.logout();
      }

      throw response;
    }),
  );
};
