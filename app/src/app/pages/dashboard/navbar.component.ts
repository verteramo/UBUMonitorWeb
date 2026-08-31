/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Principal } from '@core/models/principal';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';

/** Componente de la barra de navegación del dashboard. */
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    ThemeToggleComponent,
  ],
  styles: `
    :host {
      display: block;
      width: 100%;
    }

    mat-toolbar {
      height: 64px;
      padding: 0 16px;
      gap: 16px;
      background-color: var(--mat-sys-primary-container);
      color: var(--mat-sys-on-primary-container);
      border-bottom: 1px solid var(--mat-sys-outline-variant);
    }

    .spacer {
      flex: 1 1 auto;
    }

    .logo {
      height: 36px;
      object-fit: contain;
    }

    .title {
      font-weight: 500;
    }

    .picture {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
    }

    .picture-button {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      padding: 0;
    }
  `,
  template: `
    <mat-toolbar color="primary">
      <button matIconButton (click)="toggleSidebar.emit()" i18n-title title="Open sidebar">
        <mat-icon>menu</mat-icon>
      </button>

      <img src="logo2.png" alt="UBUMonitorWeb" class="logo" />
      <span class="title">UBUMonitorWeb</span>

      <span class="spacer"></span>

      <!-- Menú Logs -->
      <button matButton [matMenuTriggerFor]="logsMenu" i18n>
        <mat-icon>receipt_long</mat-icon> Logs
      </button>
      <mat-menu #logsMenu="matMenu">
        <button mat-menu-item>
          <mat-icon>upload_file</mat-icon>
          <span i18n>Upload</span>
        </button>
        <button mat-menu-item>
          <mat-icon>delete_sweep</mat-icon>
          <span i18n>Clear</span>
        </button>
      </mat-menu>

      <!-- Menú Export -->
      <button matButton [matMenuTriggerFor]="exportMenu" i18n>
        <mat-icon>download</mat-icon> Export
      </button>
      <mat-menu #exportMenu="matMenu">
        <button mat-menu-item>
          <mat-icon>dashboard</mat-icon>
          <span i18n>Dashboard</span>
        </button>
        <button mat-menu-item>
          <mat-icon>leaderboard</mat-icon>
          <span i18n>Ranking</span>
        </button>
        <button mat-menu-item>
          <mat-icon>group</mat-icon>
          <span i18n>Users</span>
        </button>
      </mat-menu>

      <!-- Botón Settings -->
      <button matButton (click)="openSettings.emit()" i18n>
        <mat-icon>settings</mat-icon> Settings
      </button>

      <!-- Botón Modo Claro / Oscuro -->
      <app-theme-toggle />

      <button mat-icon-button [matMenuTriggerFor]="userMenu" class="picture-button">
        <img
          [src]="principal().picture ?? 'user_blank.png'"
          [alt]="principal().fullName"
          class="picture"
        />
      </button>

      <mat-menu #userMenu>
        <h3>{{ principal().fullName }}</h3>
        <mat-divider></mat-divider>
        <button mat-menu-item (click)="changeCourse.emit()">
          <mat-icon>swap_horiz</mat-icon>
          <span i18n>Change course</span>
        </button>
        <button mat-menu-item (click)="logout.emit()">
          <mat-icon>logout</mat-icon>
          <span i18n>Logout</span>
        </button>
      </mat-menu>
    </mat-toolbar>
  `,
})
export class NavbarComponent {
  /** Usuario autenticado. */
  principal = input.required<Principal>();

  /** Evento de cambio de curso. */
  changeCourse = output<void>();

  /** Evento de cierre de sesión. */
  logout = output<void>();

  /** Evento de cambio de estado de la barra lateral. */
  toggleSidebar = output<void>();

  /** Evento para abrir la configuración. */
  openSettings = output<void>();
}
