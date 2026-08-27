/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, OnDestroy, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { TimeAgoPipe } from '../../../core/pipes/time-ago.pipe';

/** Componente de la barra de estado del dashboard. */
@Component({
  selector: 'app-statusbar',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, TimeAgoPipe],
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.scss'],
})
export class StatusbarComponent implements OnDestroy {
  /** Plataforma Moodle de inicio de sesión. */
  platform = input.required<Principal['platform']>();

  /** Curso seleccionado. */
  course = input.required<Course>();

  /** Evento de refresco. */
  refresh = output<void>();

  /** Timestamp de la última actualización */
  lastUpdate = signal(new Date().getTime());

  /** Timer para calcular el tiempo transcurrido desde la última actualización. */
  private timer: number;

  constructor() {
    // Arranque del timer
    this.timer = setInterval(() => {
      this.lastUpdate.set(new Date().getTime());
    }, 60000);
  }

  ngOnDestroy(): void {
    // Limpieza del timer
    clearInterval(this.timer);
  }
}
