/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input } from '@angular/core';

@Component({
  selector: 'app-splash-logo',
  standalone: true,
  styles: `
    .splash-overlay {
      position: fixed;
      inset: 0;
      //background-color: #ffffff;
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 9999;
      transition:
        opacity 0.4s ease-out,
        visibility 0.4s ease-out;
    }
    .splash-overlay.fade-out {
      opacity: 0;
      visibility: hidden;
    }
    .bounce-logo {
      width: 96px;
      height: 96px;
      animation: pulse-bounce 1.5s infinite ease-in-out;
    }
    @keyframes pulse-bounce {
      0%,
      100% {
        transform: scale(0.9);
      }
      50% {
        transform: scale(1.1);
      }
    }
  `,
  template: `
    <div class="splash-overlay" [class.fade-out]="loaded()">
      <img src="logo.png" alt="UBUMonitorWeb Logo" class="bounce-logo" />
    </div>
  `,
})
export class SplashLogoComponent {
  readonly loaded = input<boolean>(false);
}
