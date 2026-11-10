/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { DatasetStore } from '@core/stores/dataset.store';
import { SessionStore } from '@core/stores/session.store';
import { DashboardStore } from './dashboard.store';
import { PreferencesComponent } from './main/preferences.component';
import { WorkspaceComponent } from './main/workspace.component';
import { NavbarComponent } from './navbar.component';
import { ActivityPanelComponent } from './sidenav/activities/panel.component';
import { UserPanelComponent } from './sidenav/users/panel.component';
import { StatusbarComponent } from './statusbar.component';

/**
 * Componente del dashboard.
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

  private readonly dialog = inject(MatDialog);

  onOpenPreferences(): void {
    this.dialog.open(PreferencesComponent, {
      width: '80vw',
      height: '80vh',
      maxWidth: '1000px',
    });
  }

  /**
   * Sincroniza y guarda el estado actual del dataset en el servidor.
   */
  save(): void {}
}
