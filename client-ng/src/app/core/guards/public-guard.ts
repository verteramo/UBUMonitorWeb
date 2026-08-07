import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';

export const publicGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const $principal = authService.getPrincipal();

  if ($principal() != null) {
    return router.createUrlTree(['/dashboard']);
  }

  return true;
};
