import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AUTH_SERVICE, AuthService } from '@core/api/auth.service';
import { catchError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const authErrorStatuses = [401, 403];
  const isAuthService = req.context.get(AUTH_SERVICE) === true;

  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      if (!isAuthService && authErrorStatuses.includes(response.status)) {
        authService.logout().subscribe({
          complete: () => router.navigate(['/login']),
        });
      }

      throw response;
    }),
  );
};
