import { Component, inject } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { Course } from '@core/models/course';
import { CourseStore } from '@core/store/course.store';
import { Principal, PrincipalStore } from '@core/store/principal.store';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { StatusbarComponent } from './components/statusbar/statusbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatSidenavModule, NavbarComponent, SidebarComponent, StatusbarComponent],
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
