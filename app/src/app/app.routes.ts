import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { courseGuard } from '@core/guards/course-guard';
import { LoginFormStore } from '@core/stores/login-form.store';
import { CourseSelectionComponent } from '@pages/course-selection/course-selection.component';
import { DashboardComponent } from '@pages/dashboard/dashboard.component';
import { LoginComponent } from '@pages/login/login.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'login',
    title: $localize`Login`,
    providers: [LoginFormStore],
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
