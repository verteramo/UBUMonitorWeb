import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { WorkspaceTab, WorkspaceTabStore } from './workspace-tab.store';

@Component({
  selector: 'app-workspace',
  standalone: true,
  imports: [MatTabsModule],
  styles: ``,
  template: `
    <mat-tab-group (selectedIndexChange)="tabStore.setTab(tabs[$event])" animationDuration="0ms">
      <mat-tab label="Visual Analysis">
      </mat-tab>
      <mat-tab label="Compare">
      </mat-tab>
      <mat-tab label="Forums">
      </mat-tab>
      <mat-tab label="Dropout Risk">
      </mat-tab>
      <mat-tab label="Enrollment">
      </mat-tab>
      <mat-tab label="Events">
      </mat-tab>
      <mat-tab label="Clustering">
      </mat-tab>
    </mat-tab-group>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkspaceComponent {
  readonly tabStore = inject(WorkspaceTabStore);

  readonly tabs: WorkspaceTab[] = [
    'visual', 'compare', 'forums', 'risk', 'enrollment', 'events', 'clustering'
  ];
}
