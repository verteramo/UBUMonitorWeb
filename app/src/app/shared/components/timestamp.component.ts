import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { TimeAgoPipe } from '@core/pipes/time-ago.pipe';

/**
 * Componente para mostrar timestamps formateados y relativos.
 */
@Component({
  selector: 'app-timestamp',
  standalone: true,
  imports: [DatePipe, TimeAgoPipe],
  template: `
    @if (!value()) {
      {{ value() | timeAgo }}
    } @else {
      {{ value()! * 1000 | date: 'dd/MM/yyyy' }} ({{ value() | timeAgo }})
    }
  `,
})
export class TimestampComponent {
  /**
   * Valor del timestamp en segundos.
   */
  readonly value = input.required<number | null>();
}
