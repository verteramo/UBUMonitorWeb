/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SessionStore } from '@core/stores/session.store';
import { SettingsStore } from '@core/stores/settings.store';
import { NavbarComponent } from './navbar.component';
import { ActivityPanelComponent } from './sidenav/panel-activities/activity-panel.component';
import { UserPanelComponent } from './sidenav/panel-users/user-panel.component';
import { StatusbarComponent } from './statusbar.component';

/**
 * Componente del dashboard.
 * La gestión de las preferencias se realiza mediante el SettingsStore
 * (persistencia en localStorage).
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NavbarComponent,
    StatusbarComponent,
    MatSidenavModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    UserPanelComponent,
    ActivityPanelComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  #dialog = inject(MatDialog);
  readonly store = inject(SettingsStore);
  readonly session = inject(SessionStore);

  onRefresh(): void {
    console.log('Refresh from statusbar');
  }

  onOpenSettings(): void {
    // this.#dialog.open(SettingsComponent, {
    //   width: '1200px',
    //   height: '90vh',
    // })
  }
}
