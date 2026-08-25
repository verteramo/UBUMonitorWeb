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
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
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
}
