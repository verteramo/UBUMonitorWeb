import { Routes } from '@angular/router';
import { courseGuard } from '@core/guards/course-guard';
import { privateGuard } from '@core/guards/private-guard';
import { publicGuard } from '@core/guards/public-guard';
import { CourseSelectionComponent } from '@features/course-selection/course-selection.component';
import { DashboardComponent } from '@features/dashboard/dashboard.component';
import { LoginComponent } from '@features/login/login.component';

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
    canActivate: [publicGuard],
  },
  {
    path: 'course-selection',
    title: $localize`Course selection`,
    component: CourseSelectionComponent,
    canActivate: [privateGuard],
  },
  {
    path: 'dashboard',
    title: $localize`Dashboard`,
    component: DashboardComponent,
    canActivate: [privateGuard, courseGuard],
  },
];
