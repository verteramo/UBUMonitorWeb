import { Component, inject } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { DashboardStore } from '@core/stores/dashboard.store';
import { SessionStore } from '@core/stores/session.store';
import { NavbarComponent } from './components/navbar/navbar.component';
import { UsersPanelComponent } from './components/sidenav/users-panel/users-panel.component';
import { StatusbarComponent } from './components/statusbar/statusbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NavbarComponent,
    StatusbarComponent,
    MatSidenavModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    UsersPanelComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  readonly store = inject(DashboardStore);
  readonly session = inject(SessionStore);

  onRefresh() {
    console.log('Refresh from statusbar');
  }
}
