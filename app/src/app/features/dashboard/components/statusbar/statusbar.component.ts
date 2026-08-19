import { Component, input, OnDestroy, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TimeAgoPipe } from '../../../../shared/pipes/time-ago.pipe';

@Component({
  selector: 'app-statusbar',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, TimeAgoPipe],
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.scss'],
})
export class StatusbarComponent implements OnDestroy {
  course = input.required<string>();
  siteUrl = input.required<string>();
  siteName = input.required<string>();
  refresh = output<void>();

  isOnline = signal(true);
  lastUpdate = signal(new Date());
  private timer: number;

  constructor() {
    this.timer = setInterval(() => {
      this.lastUpdate.set(new Date());
    }, 60000);
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}
