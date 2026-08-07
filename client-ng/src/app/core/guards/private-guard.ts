import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';

export const privateGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const $principal = authService.getPrincipal();

  if ($principal() != null) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
