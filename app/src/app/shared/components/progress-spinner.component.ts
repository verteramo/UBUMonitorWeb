/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

/** Progress Spiner con una configuración y estilos normalizados. */
@Component({
  selector: 'app-progress-spinner',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  template: `
    <mat-spinner diameter="48" />
    <ng-content />
  `,
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      flex: 1;
      height: 100%;
      align-items: center;
      justify-content: center;
      gap: 16px;
      color: var(--mat-sys-on-surface-variant);
    }
  `,
})
export class ProgressSpinnerComponent {}
