/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, output } from '@angular/core';
import { MatCheckbox } from '@angular/material/checkbox';

@Component({
  selector: 'app-string-item',
  imports: [MatCheckbox],
  styles: `
    article {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 2px 12px;
      border-bottom: 1px solid var(--mat-sys-outline-variant);
      cursor: pointer;
      transition: background-color 0.2s ease;
    }

    article:hover {
      background-color: var(--mat-sys-surface-container-highest, rgba(0, 0, 0, 0.04));
    }

    article.selected {
      background-color: var(--mat-sys-primary-container, rgba(0, 0, 0, 0.08));
    }

    span {
      flex: 1;
      font-size: 13px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: var(--mat-sys-on-surface);
    }

    mat-checkbox {
      height: 32px;
      display: flex;
      align-items: center;
    }
  `,
  template: `
    <article [class.selected]="selected()" (click)="toggle.emit(item())">
      <mat-checkbox [checked]="selected()" style="pointer-events: none"></mat-checkbox>

      <span [title]="item()">{{ item() }}</span>
    </article>
  `,
})
export default class StringItemComponent {
  item = input.required<string>();
  selected = input<boolean>();
  toggle = output<string>();
}
