import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { CourseStore } from '@core/store/course.store';

/**
 * Guarda que asegura el flujo de navegación, redirigiendo a la selección
 * de curso o al dashboard dependiendo de si existe un curso activo en el store.
 *
 * @param requiresCourse Indica si la ruta exige tener un curso previamente seleccionado.
 * @returns Función de guarda que permite el acceso o genera un árbol de redirección.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const courseGuard =
  (requiresCourse: boolean): CanActivateFn =>
  (route, state) => {
    const router = inject(Router);
    const course = inject(CourseStore).$value();

    if (requiresCourse && !course) {
      return router.createUrlTree(['/course-selection']);
    }

    if (!requiresCourse && course) {
      return router.createUrlTree(['/dashboard']);
    }

    return true;
  };
