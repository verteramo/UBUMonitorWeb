import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { courseGuard } from '@core/guards/course-guard';
import { DashboardComponent } from '@features/dashboard/dashboard.component';
import { LoginComponent } from '@features/login/login.component';
import { CourseSelectionComponent } from '../../course-selection/course-selection.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'login',
    title: $localize`Login`,
    component: LoginComponent,
    canActivate: [authGuard(false)],
  },
  {
    path: 'course-selection',
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
