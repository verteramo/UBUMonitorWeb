/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { ChangeDetectionStrategy, Component, model } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';

@Component({
  selector: 'app-workspace',
  standalone: true,
  imports: [MatTabsModule],
  styles: `
    ::ng-deep .mat-mdc-tab {
      min-width: 0 !important;
      padding: 0 8px !important;
    }

    ::ng-deep .mdc-tab__text-label {
      font-size: 12px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  `,
  template: `
    <mat-tab-group [(selectedIndex)]="tabIndex" animationDuration="0ms">
      <mat-tab i18n-label label="Visual Analysis"> </mat-tab>
      <mat-tab i18n-label label="Compare"> </mat-tab>
      <mat-tab i18n-label label="Forums"> </mat-tab>
      <mat-tab i18n-label label="Dropout Risk"> </mat-tab>
      <mat-tab i18n-label label="Enrollment"> </mat-tab>
      <mat-tab i18n-label label="Events"> </mat-tab>
      <mat-tab i18n-label label="Clustering"> </mat-tab>
    </mat-tab-group>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkspaceComponent {
  readonly tabIndex = model.required<number>();
}
