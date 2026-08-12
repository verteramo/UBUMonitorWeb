import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PrincipalStore } from '@core/store/principal.store';

export const privateGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const principalStore = inject(PrincipalStore);

  if (principalStore.$value() != null) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
