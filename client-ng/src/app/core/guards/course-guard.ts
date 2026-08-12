import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { CourseStore } from '@core/store/course.store';

export const courseGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const courseStore = inject(CourseStore);

  if (courseStore.$value() != null) {
    return true;
  }

  return router.createUrlTree(['/course-selection']);
};
