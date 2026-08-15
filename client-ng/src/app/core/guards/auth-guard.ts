import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { PrincipalStore } from '@core/store/principal.store';

/**
 * Guarda que controla el flujo de autenticación, redirigiendo al inicio
 * de sesión o al dashboard dependiendo de si existe un usuario activo.
 *
 * @param requiresPrincipal Indica si la ruta exige que el usuario esté autenticado.
 * @returns Función de guarda que permite el acceso o genera un árbol de redirección.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const authGuard =
  (requiresPrincipal: boolean): CanActivateFn =>
  (route, state) => {
    const router = inject(Router);
    const principal = inject(PrincipalStore).$value();

    if (requiresPrincipal && !principal) {
      return router.createUrlTree(['/login']);
    }

    if (!requiresPrincipal && principal) {
      return router.createUrlTree(['/dashboard']);
    }

    return true;
  };
