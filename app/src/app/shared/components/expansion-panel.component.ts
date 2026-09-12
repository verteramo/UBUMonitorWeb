import { Component, input, model } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';

@Component({
  selector: 'app-expansion-panel',
  imports: [MatExpansionModule, ProgressSpinnerComponent],
  host: {
    '[class.is-expanded]': 'expanded()',
  },
  styles: `
    :host {
      display: flex;
      flex-direction: column;
    }

    :host(.is-expanded) {
      flex: 1;
      min-height: 0;
    }

    ::ng-deep mat-expansion-panel {
      border-radius: 4px !important;
      border: 1px solid var(--mat-sys-outline-variant) !important;
      --mat-expansion-container-elevation-shadow: none;
      --mat-expansion-header-expanded-state-height: 30px;
      --mat-expansion-header-collapsed-state-height: 30px;

      mat-expansion-panel-header {
        background-color: var(--mat-sys-surface-container-high) !important;
      }

      .mat-expansion-panel-body {
        padding: 0;
      }

      &.mat-expanded {
        display: grid !important;
        grid-template-rows: max-content minmax(0, 1fr) !important;
        flex: 1 !important;
        margin: 0;

        mat-expansion-panel-header {
          border-bottom-left-radius: 0 !important;
          border-bottom-right-radius: 0 !important;
          border-bottom: 1px solid var(--mat-sys-outline-variant) !important;
        }

        .mat-expansion-panel-content,
        .mat-expansion-panel-content > div,
        .mat-expansion-panel-body {
          display: flex !important;
          flex-direction: column !important;
          flex: 1 !important;
          min-height: 0 !important;
          padding: 0 !important;
          margin: 0 !important;
          overflow: hidden !important;
        }
      }
    }

    ::ng-deep header {
      flex-shrink: 0;
    }

    .scroll-area {
      display: flex;
      flex-direction: column;
      flex: 1;
      min-height: 0;
      overflow-y: auto;
    }
  `,
  template: `
    <mat-expansion-panel
      [expanded]="expanded()"
      (opened)="expanded.set(true)"
      (closed)="expanded.set(false)"
    >
      <mat-expansion-panel-header>
        <mat-panel-title i18n>{{ title() }}</mat-panel-title>
      </mat-expansion-panel-header>

      <ng-content select="header"></ng-content>

      <div class="scroll-area">
        @if (loading()) {
          <app-progress-spinner style="margin: auto;" i18n>Loading...</app-progress-spinner>
        } @else {
          <ng-content select="main"></ng-content>
        }
      </div>
    </mat-expansion-panel>
  `,
})
export class ExpansionPanelComponent {
  readonly title = input.required<string>();
  readonly expanded = model.required<boolean>();
  readonly loading = input.required<boolean>();
}
