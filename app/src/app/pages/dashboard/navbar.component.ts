/*
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
import { User } from '@core/models/user';
import { AvatarComponent } from '@shared/components/avatar.component';
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
    AvatarComponent,
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

      <img src="logo-alt.png" alt="UBUMonitorWeb" class="logo" />
      <span class="title">UBUMonitorWeb</span>

      <span class="spacer"></span>

      <!-- Botón Save -->
      <button matButton (click)="save.emit()" i18n><mat-icon>save</mat-icon> Save</button>

      <!-- Botón Settings -->
      <button matButton (click)="openPreferences.emit()" i18n>
        <mat-icon>tune</mat-icon> Preferences
      </button>

      <!-- Botón Modo Claro / Oscuro -->
      <app-theme-toggle />

      <button mat-icon-button [matMenuTriggerFor]="userMenu" class="picture-button">
        <app-avatar [data]="user()" />
      </button>

      <mat-menu #userMenu>
        <h3>{{ user().fullName }}</h3>
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
  user = input.required<User>();

  /** Evento de cambio de curso. */
  changeCourse = output<void>();

  /** Evento de cierre de sesión. */
  logout = output<void>();

  /** Evento de cambio de estado de la barra lateral. */
  toggleSidebar = output<void>();

  /** Evento para abrir la configuración de las preferencias del usuario. */
  openPreferences = output<void>();

  /** Evento para guardar el estado actual. */
  save = output<void>();
}
