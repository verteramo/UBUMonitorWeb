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

      const days = Math.floor(diff / DAY_MS);
      const hours = Math.floor((diff % DAY_MS) / HOUR_MS);
      const minutes = Math.floor((diff % HOUR_MS) / MINUTE_MS);

      let result = '';

      if (days) {
        result += `${days}d `;
      }
      if (hours) {
        result += `${hours}h `;
      }
      if (minutes) {
        result += `${minutes}m`;
      }

      return result?.trim() || $localize`Just now`;
    }
    return $localize`Never`;
  }
}
