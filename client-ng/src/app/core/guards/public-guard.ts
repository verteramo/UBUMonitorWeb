import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PrincipalStore } from '@core/store/principal.store';

export const publicGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const principalStore = inject(PrincipalStore);

  if (principalStore.$value() != null) {
    return router.createUrlTree(['/dashboard']);
  }

  return true;
};
