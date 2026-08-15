import { DatePipe } from '@angular/common';
import { Component, Input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-status-bar',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, DatePipe],
  template: `
    <div class="status-container">
      <div class="status-left">
        <button mat-icon-button [style.color]="isOnline() ? 'green' : 'red'" title="Conexión">
          <mat-icon>{{ isOnline() ? 'wifi' : 'wifi_off' }}</mat-icon>
        </button>
        <span class="course-name">{{ courseName || 'Sin asignatura' }}</span>
        <span class="separator">|</span>
        <span class="host-name">{{ host || 'Sin host' }}</span>
      </div>
      <div class="status-right">
        <span>Última actualización: {{ lastUpdate() | date: 'dd/MM/yyyy HH:mm:ss' }}</span>
      </div>
    </div>
  `,
  styles: [
    `
      .status-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 100%;
        padding: 0 16px;
        font-size: 13px;
        color: #555;
      }
      .status-left,
      .status-right {
        display: flex;
        align-items: center;
        gap: 8px;
      }
      .separator {
        color: #ccc;
      }
      .course-name {
        font-weight: 500;
      }
    `,
  ],
})
export class StatusBarComponent {
  @Input() courseName?: string;
  @Input() host?: string | null;

  isOnline = signal(true);
  lastUpdate = signal(new Date());
}
