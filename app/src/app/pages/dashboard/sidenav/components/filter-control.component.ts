import { Component, input, model, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-filter-control',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatBadgeModule,
    MatMenuModule,
    MatDividerModule,
  ],
  styles: `
    .filter-row {
      display: flex;
      flex-direction: row;
      align-items: center;
      gap: 12px;
      padding: 8px 12px;
      background-color: var(--mat-sys-surface-container);
      border-bottom: 1px solid var(--mat-sys-outline-variant);

      mat-checkbox {
        width: 36px;
        height: 30px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      mat-form-field {
        flex: 1;
        min-width: 0;
        background-color: var(--mat-sys-surface);
        border-radius: 4px;

        ::ng-deep {
          .mat-mdc-text-field-wrapper {
            padding-top: 0 !important;
            padding-bottom: 0 !important;
          }
          .mat-mdc-form-field-flex {
            height: 30px !important;
            align-items: center !important;
          }
          .mat-mdc-form-field-infix {
            padding: 0 !important;
            min-height: auto !important;
            display: flex;
            align-items: center;
          }
        }
      }

      button {
        width: 30px;
        height: 30px;
        padding: 0;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    ::ng-deep .filters-menu {
      padding: 8px;
      display: flex;
      flex-direction: column;
      gap: 8px;
      min-width: 200px;
    }
  `,
  template: `
    <div class="filter-row">
      <mat-checkbox
        i18n-title
        title="Select all"
        [checked]="checked()"
        [indeterminate]="indeterminate()"
        (change)="toggleAll.emit()"
      >
      </mat-checkbox>

      <mat-form-field appearance="outline" subscriptSizing="dynamic">
        <input matInput [placeholder]="placeholder()" [(ngModel)]="term" />
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>

      <button matIconButton [matMenuTriggerFor]="filters">
        <mat-icon
          aria-hidden="false"
          [matBadge]="badge()"
          [matBadgeHidden]="badge() === 0"
          matBadgeSize="medium"
          matBadgeColor="accent"
        >
          filter_list
        </mat-icon>
      </button>
    </div>

    <mat-menu #filters="matMenu" xPosition="before">
      <div class="filters-menu" (click)="$event.stopPropagation()">
        <h3 i18n>Filters</h3>
        <mat-divider></mat-divider>
        <!-- Filtros específicos -->
        <ng-content></ng-content>
      </div>
    </mat-menu>
  `,
})
export class FilterControlComponent {
  /** Configuración de estado */
  placeholder = input<string>('');
  checked = input<boolean>(false);
  indeterminate = input<boolean>(false);
  badge = input<number>(0);

  /** Término de búsqueda (two-way binding). */
  term = model<string>('');

  /** Evento emitido al pulsar el checkbox. */
  toggleAll = output<void>();
}
