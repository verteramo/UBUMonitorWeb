/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { DatasetStore } from '@core/stores/dataset.store';
import { SessionStore } from '@core/stores/session.store';
import { DashboardStore } from './dashboard.store';
import { WorkspaceTabStore } from './main/workspace-tab.store';
import { WorkspaceComponent } from './main/workspace.component';
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
    WorkspaceComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  providers: [DashboardStore],
})
export class DashboardComponent {
  readonly store = inject(DashboardStore);
  readonly session = inject(SessionStore);
  readonly dataset = inject(DatasetStore);
  readonly tabStore = inject(WorkspaceTabStore);

  onOpenSettings(): void {
    // this.#dialog.open(SettingsComponent, {
    //   width: '1200px',
    //   height: '90vh',
    // })
  }
}
