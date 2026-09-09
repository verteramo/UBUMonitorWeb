/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '@core/stores/session.store';

/**
 * Guarda que asegura el flujo de navegación redirigiendo
 * al login, a la página de selección de curso o al dashboard,
 * dependiendo de si existe usuario autenticado y curso seleccionado.
 */
export const sessionGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const session = inject(SessionStore);
  const location = session.location();

  if (!state.url.startsWith(location)) {
    return router.createUrlTree([location]);
  }

  return true;
};
