/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, DestroyRef, inject, input, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { TimeAgoPipe } from '../../core/pipes/time-ago.pipe';

/** Componente de la barra de estado del dashboard. */
@Component({
  selector: 'app-statusbar',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, TimeAgoPipe],
  styles: `
    :host {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 4px 16px;
      font-size: 13px;
      flex: 0 0 auto;
      background-color: var(--mat-sys-primary-container);
      color: var(--mat-sys-on-primary-container);
      border-top: 1px solid var(--mat-sys-outline-variant);
    }

    .status-left,
    .status-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .separator {
      color: var(--mat-sys-outline-variant);
    }

    a[matButton],
    a[matIconButton] {
      color: inherit;
      text-decoration: none;
    }
  `,
  template: `
    <div class="status-left">
      <span class="host-name">
        <a matButton target="_blank" [href]="principal().platform.url" [title]="principal().platform.release">
          {{ principal().platform.name }}
        </a>
      </span>
      <span class="separator">|</span>
      <span>{{ course().category }}</span>
      <span class="separator">|</span>
      <span>{{ course().name }}</span>
    </div>

    <div class="status-right">
      <span>Last update: {{ lastUpdate() | timeAgo }}</span>

      <button matIconButton (click)="refresh.emit()" i18n-title title="Update">
        <mat-icon>sync</mat-icon>
      </button>

      <span class="separator">|</span>

      <a
        matIconButton
        target="_blank"
        i18n-title
        title="User manual"
        href="https://ubumonitordocs.readthedocs.io/es/latest/"
      >
        <mat-icon>menu_book</mat-icon>
      </a>

      <a
        matIconButton
        target="_blank"
        i18n-title
        title="Repository"
        href="https://github.com/verteramo/UBUMonitorWeb"
      >
        <mat-icon>code</mat-icon>
      </a>
    </div>
  `,
})
export class StatusbarComponent {
  private destroyRef = inject(DestroyRef);

  /** Plataforma Moodle de inicio de sesión. */
  principal = input.required<Principal>();

  /** Curso seleccionado. */
  course = input.required<Course>();

  /** Evento de refresco. */
  protected refresh = output<void>();

  /** Timestamp de la última actualización */
  protected lastUpdate = signal(Date.now());

  constructor() {
    // Arranque del timer
    const timer = setInterval(() => {
      this.lastUpdate.set(Date.now());
    }, 60000);

    this.destroyRef.onDestroy(() => {
      clearInterval(timer);
    });
  }
}
