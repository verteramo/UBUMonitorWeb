import { Component, inject } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
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
  private sessionStore = inject(SessionStore);

  get principal() {
    return this.sessionStore.principal() as Principal;
  }

  get course() {
    return this.sessionStore.course() as Course;
  }

  changeCourse() {
    this.sessionStore.clearCourse();
    this.router.navigate(['/course-selection']);
  }

  logout() {
    this.authService.logout();
  }
}
