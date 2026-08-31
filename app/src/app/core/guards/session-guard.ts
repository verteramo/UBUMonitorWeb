import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '@core/stores/session.store';

/**
 * Guarda que asegura el flujo de navegación redirigiendo al login o al
 * dashboard, dependiendo de si existe usuario autenticado y/o curso seleccionado.
 */
export const sessionGuard: CanActivateFn = (_, { url }) => {
  const router = inject(Router);
  const { targetRoute } = inject(SessionStore);

  if (!url.startsWith(targetRoute())) {
    return router.createUrlTree([targetRoute()]);
  }

  return true;
};
