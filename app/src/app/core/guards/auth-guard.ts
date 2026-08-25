/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '@core/stores/session.store';

/**
 * Guarda que asegura el flujo de navegación, redirigiendo al login
 * o al dashboard, dependiendo de si existe un usuario autenticado.
 *
 * Está construida como una función que devuelve la guarda para poder
 * establecer el requerimiento: `authGuard(true)` o `authGuard(false)`.
 *
 * @param authenticated Indica si la ruta exige que el usuario esté autenticado.
 * @returns Función de guarda.
 */
export const authGuard =
  (authenticated: boolean): CanActivateFn =>
  (route, state) => {
    const router = inject(Router);
    const principal = inject(SessionStore).principal();

    if (authenticated && !principal) {
      return router.createUrlTree(['/login']);
    }

    if (!authenticated && principal) {
      return router.createUrlTree(['/dashboard']);
    }

    return true;
  };
