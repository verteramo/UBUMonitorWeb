import { Component, computed, inject } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSidenavModule } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { AuthService } from '@core/services/auth.service';
import { SessionStore } from '@core/stores/session.store';
import { NavbarComponent } from './components/navbar/navbar.component';
import { UsersListComponent } from './components/sidenav/users-list/users-list.component';
import { StatusbarComponent } from './components/statusbar/statusbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatSidenavModule,
    NavbarComponent,
    MatExpansionModule,
    StatusbarComponent,
    UsersListComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  private router = inject(Router);
  private authService = inject(AuthService);
  private sessionStore = inject(SessionStore);

  principal = computed(() => this.sessionStore.principal() as Principal);

  course = computed(() => this.sessionStore.course() as Course);

  changeCourse() {
    this.sessionStore.clearCourse();
    this.router.navigate(['/course-selection']);
  }

  logout() {
    this.authService.logout();
  }
}
