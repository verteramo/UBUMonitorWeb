/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Routes } from '@angular/router';
import { sessionGuard } from '@core/guards/session-guard';
import { CourseSelectionComponent } from '@pages/course-selection/course-selection.component';
import { DashboardComponent } from '@pages/dashboard/dashboard.component';
import { LoginComponent } from '@pages/login/login.component';
import { LoginStore } from '@pages/login/login.store';

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
    canActivate: [sessionGuard],
  },
  {
    path: 'course',
    title: $localize`Course selection`,
    component: CourseSelectionComponent,
    canActivate: [sessionGuard],
  },
  {
    path: 'dashboard',
    title: $localize`Dashboard`,
    component: DashboardComponent,
    canActivate: [sessionGuard],
  },
];
