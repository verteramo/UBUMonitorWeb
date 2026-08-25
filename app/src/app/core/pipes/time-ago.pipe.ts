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

/** Unidades de segundos. */
type Unit = 'ms' | 's';

/**
 * Pipe para formatear timestaps.
 *
 * Moodle devuelve timestamps de tipo int;
 * es un dato ligero y conveniente, con este pipe
 * se elimina la necesidad de convertirlos a Date
 * y permite formatearlos como el tiempo que ha pasado.
 *
 * Permite pasarle la unidad de tiempo ya que, por ejemplo,
 * Date.getTime devuelve milisegundos y los timestamps de
 * Moodle están en segundos.
 *
 * Todas las cadenas son localizables.
 */
@Pipe({
  name: 'timeAgo',
  standalone: true,
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: number | null | undefined, unit: Unit = 'ms'): string {
    if (value) {
      const ms = unit === 's' ? value * 1000 : value;
      const diff = new Date().getTime() - ms;

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
