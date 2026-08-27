/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { courseGuard } from '@core/guards/course-guard';
import { LoginStore } from '@core/stores/login.store';
import { CourseSelectionComponent } from '@pages/course-selection/course-selection.component';
import { DashboardComponent } from '@pages/dashboard/dashboard.component';
import { LoginComponent } from '@pages/login/login.component';

/**
 * Configuración de las rutas de la aplicación.
 * Aquí se pueden configurar las rutas, títulos, componentes y guardas.
 *
 * @see https://angular.dev/guide/routing
 */
export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'login',
    title: $localize`Login`,
    providers: [LoginStore],
    component: LoginComponent,
    canActivate: [authGuard(false)],
  },
  {
    path: 'course',
    title: $localize`Course selection`,
    component: CourseSelectionComponent,
    canActivate: [authGuard(true), courseGuard(false)],
  },
  {
    path: 'dashboard',
    title: $localize`Dashboard`,
    component: DashboardComponent,
    canActivate: [authGuard(true), courseGuard(true)],
  },
];
