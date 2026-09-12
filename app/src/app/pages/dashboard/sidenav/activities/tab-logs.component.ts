import { Component, inject } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { withTabs } from '@core/stores/features/tab.feature';
import { signalStore } from '@ngrx/signals';
import { TabLogsSectionsComponent } from './tab-logs-sections.component';

const Store = signalStore(withTabs(['main', 'sections']));

@Component({
  selector: 'app-tab-logs',
  providers: [Store],
  imports: [MatTabsModule, TabLogsSectionsComponent],
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      flex: 1;
      min-height: 0;
      height: 100%;
    }

    mat-tab-group {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-height: 0;
    }

    header {
      flex-shrink: 0;
    }

    main {
      display: flex;
      flex-direction: column;
      flex: 1;
      min-height: 0;
      overflow-y: auto;
    }

    ::ng-deep .mat-mdc-tab {
      min-height: 36px !important;
      height: 36px !important;
    }
  `,
  template: `
    <mat-tab-group
      [selectedIndex]="store.tabIndex()"
      (selectedIndexChange)="store.setTabIndex($event)"
    >
      <mat-tab i18n-label label="Main"> </mat-tab>

      <mat-tab i18n-label label="Sections">
        <app-tab-logs-sections />
      </mat-tab>
    </mat-tab-group>
  `,
})
export class TabLogsComponent {
  readonly store = inject(Store);
}
