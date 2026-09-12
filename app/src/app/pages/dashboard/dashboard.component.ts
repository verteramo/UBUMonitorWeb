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
import { openFilePicker, saveFilePicker, UmwDatasetType } from '@core/utils/window.utils';
import { DashboardStore } from './dashboard.store';
import { WorkspaceComponent } from './main/workspace.component';
import { NavbarComponent } from './navbar.component';
import { ActivityPanelComponent } from './sidenav/activities/panel.component';
import { UserPanelComponent } from './sidenav/users/panel.component';
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

  onOpenSettings(): void {
    // this.#dialog.open(SettingsComponent, {
    //   width: '1200px',
    //   height: '90vh',
    // })
  }

  async exportDataset(): Promise<void> {
    try {
      const data = this.dataset.encrypt();
      await saveFilePicker({ suggestedName: 'dataset.umw', types: [UmwDatasetType] }, data);
    } catch (e) {
      console.error('Error writing file', e);
    }
  }

  async importDataset(): Promise<void> {
    try {
      const data = await openFilePicker({ types: [UmwDatasetType] });
      this.dataset.decrypt(data);
    } catch (e) {
      console.error('Error reading file', e);
    }
  }
}
