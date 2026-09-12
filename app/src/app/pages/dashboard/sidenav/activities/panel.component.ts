import { Component, inject, input, model } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { withTabs } from '@core/stores/features/tab.feature';
import { signalStore } from '@ngrx/signals';
import { ExpansionPanelComponent } from '@shared/components/expansion-panel.component';
import { TabLogsComponent } from './tab-logs.component';

const Store = signalStore(withTabs(['logs', 'grades', 'activities']));

@Component({
  selector: 'app-activity-panel',
  providers: [Store],
  imports: [MatTabsModule, ExpansionPanelComponent, TabLogsComponent],
  styles: `
    :host {
      display: contents;
    }

    main {
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

    ::ng-deep .mat-mdc-tab {
      min-height: 36px !important;
      height: 36px !important;
    }

    ::ng-deep .mat-mdc-tab-body-wrapper {
      display: flex !important;
      flex-direction: column !important;
      flex: 1 !important;
    }

    ::ng-deep .mat-mdc-tab-body {
      display: flex !important;
      flex-direction: column !important;
      flex: 1 !important;
    }

    ::ng-deep .mat-mdc-tab-body-content {
      display: flex !important;
      flex-direction: column !important;
      flex: 1 !important;
      height: 100% !important;
    }
  `,
  template: `
    <app-expansion-panel
      i18n-title
      title="Activities"
      [(expanded)]="expanded"
      [loading]="loading()"
    >
      <main>
        <mat-tab-group
          [selectedIndex]="store.tabIndex()"
          (selectedIndexChange)="store.setTabIndex($event)"
        >
          <mat-tab i18n-label label="Logs">
            <app-tab-logs />
          </mat-tab>

          <mat-tab i18n-label label="Grades"> </mat-tab>

          <mat-tab i18n-label label="Activity completion"> </mat-tab>
        </mat-tab-group>
      </main>
    </app-expansion-panel>
  `,
})
export class ActivityPanelComponent {
  readonly store = inject(Store);
  readonly loading = input<boolean>(false);
  readonly expanded = model.required<boolean>();
}
