/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Pipe, PipeTransform } from '@angular/core';

/*
 * Constantes de utilidad para operar con los timestamps.
 */
const MINUTE_MS = 60 * 1000;
const HOUR_MS = 60 * MINUTE_MS;
const DAY_MS = 24 * HOUR_MS;

/**
 * Pipe para formatear timestaps.
 * Todas las cadenas son localizables.
 */
@Pipe({
  name: 'timeAgo',
  standalone: true,
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    if (value) {
      const diff = new Date().getTime() - value;

      switch (true) {
        case diff >= DAY_MS:
          return `${Math.floor(diff / DAY_MS)} days`;
        case diff >= HOUR_MS:
          return `${Math.floor(diff / HOUR_MS)} hours`;
        case diff >= MINUTE_MS:
          return `${Math.floor(diff / MINUTE_MS)} minutes`;
        default:
          return $localize`Just now`;
      }
    }

    return $localize`Never`;
  }
}
