/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, output } from '@angular/core';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';

type Item = {
  id: number;
  name: string | null;
  isVisible: boolean;
  isUserVisible: boolean;
}

@Component({
  selector: 'app-item',
  imports: [MatIcon, MatCheckbox],
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

    mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
      color: var(--mat-sys-outline, #757575);
    }

    mat-checkbox {
      height: 32px;
      display: flex;
      align-items: center;
    }
  `,
  template: `
    <article [class.selected]="selected()" (click)="toggle.emit(item().id)">
      <mat-checkbox [checked]="selected()" style="pointer-events: none"></mat-checkbox>

      <span [title]="item().name">{{ item().name }}</span>

      @if (item().isUserVisible) {
        <mat-icon i18n-title title="Public">public</mat-icon>
      }

      @if (item().isVisible) {
        <mat-icon i18n-title title="Visible">visibility</mat-icon>
      } @else {
        <mat-icon i18n-title title="Hidden">visibility_off</mat-icon>
      }
    </article>
  `,
})
export default class ItemComponent {
  item = input.required<Item>();
  selected = input<boolean>();
  toggle = output<number>();
}
