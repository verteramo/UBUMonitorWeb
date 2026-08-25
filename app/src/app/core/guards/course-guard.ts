/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from '@core/stores/session.store';

/**
 * Guarda que asegura el flujo de navegación, redirigiendo a la selección
 * de curso o al dashboard dependiendo de si existe un curso activo en el store.
 *
 * Está construida como una función que devuelve la guarda para poder
 * establecer el requerimiento: `courseGuard(true)` o `courseGuard(false)`.
 *
 * @param selection Indica si la ruta exige tener un curso previamente seleccionado.
 * @returns Función de guarda.
 */
export const courseGuard =
  (selection: boolean): CanActivateFn =>
  (route, state) => {
    const router = inject(Router);
    const course = inject(SessionStore).course();

    if (selection && !course) {
      return router.createUrlTree(['/course-selection']);
    }

    if (!selection && course) {
      return router.createUrlTree(['/dashboard']);
    }

    return true;
  };
