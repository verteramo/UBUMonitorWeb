import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { Course } from '@core/models/course';
import { CourseStore } from '@core/store/course.store';
import { Principal, PrincipalStore } from '@core/store/principal.store';
import { ActivitySelectionComponent } from './components/activity-selection/activity-selection.component';
import { ChartsViewComponent } from './components/charts-view/charts-view.component';
import { StatusBarComponent } from './components/status-bar/status-bar.component';
import { UsersListComponent } from './components/users-list/users-list.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDividerModule,
    UsersListComponent,
    ActivitySelectionComponent,
    ChartsViewComponent,
    StatusBarComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  private router = inject(Router);
  private authService = inject(AuthService);
  private principalStore = inject(PrincipalStore);
  private courseStore = inject(CourseStore);

  get principal() {
    return this.principalStore.$value() as Principal;
  }

  get course() {
    return this.courseStore.$value() as Course;
  }

  changeCourse() {
    this.courseStore.clear();
    this.router.navigate(['/course-selection']);
  }

  logout() {
    this.authService.logout();
  }
}
