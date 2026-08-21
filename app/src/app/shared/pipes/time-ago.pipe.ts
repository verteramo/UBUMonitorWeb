import { Pipe, PipeTransform } from '@angular/core';

const MINUTE_MS = 60 * 1000;
const HOUR_MS = 60 * MINUTE_MS;
const DAY_MS = 24 * HOUR_MS;

@Pipe({
  name: 'timeAgo',
  standalone: true,
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: Date | null | undefined): string {
    if (value) {
      const diff = new Date().getTime() - value.getTime();

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
