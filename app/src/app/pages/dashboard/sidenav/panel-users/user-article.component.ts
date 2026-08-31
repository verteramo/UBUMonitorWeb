/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { User } from '@core/models/user';
import { TimeAgoPipe } from '@core/pipes/time-ago.pipe';

/** Componente que renderiza los datos de un usuario en la lista de usuarios. */
@Component({
  selector: 'app-user-article',
  imports: [MatIconModule, MatCheckboxModule, TimeAgoPipe, MatButtonModule],
  styles: `
    article {
      cursor: pointer;
      display: flex;
      flex-direction: row;
      align-items: center;
      gap: 8px;
      padding: 10px 12px;
      border-bottom: 1px solid var(--mat-sys-outline-variant);

      &.selected,
      &:hover {
        background-color: var(--mat-sys-secondary-container);

        .avatar-action-container {
          img {
            display: none;
          }
          mat-checkbox {
            display: flex !important;
          }
        }
      }

      .avatar-action-container {
        width: 36px;
        height: 36px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          object-fit: cover;
        }

        mat-checkbox {
          display: none !important;
        }
      }
    }

    .user-details {
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 2px;
      flex: 1;
      min-width: 0;

      strong,
      small {
        white-space: nowrap;
        overflow: hidden;
        text-align: left;
        text-overflow: ellipsis;
        color: var(--mat-sys-on-surface);
      }

      strong {
        font-weight: 500;
        font-size: 14px;
      }

      small {
        font-size: 12px;
        line-height: 1.2;
      }
    }
  `,
  template: `
    <article [class.selected]="selected()">
      <div class="avatar-action-container" (click)="toggle.emit(user().id)">
        <img [src]="user().picture || 'user_blank.png'" [alt]="user().fullName" />
        <mat-checkbox [checked]="selected()" style="pointer-events: none"></mat-checkbox>
      </div>

      <div class="user-details">
        <strong>{{ user().fullName }}</strong>
        <small i18n>Course: {{ user().lastCourseAccess | timeAgo: 's' }}</small>
        <small i18n>Platform: {{ user().lastAccess | timeAgo: 's' }}</small>
      </div>

      <button
        matIconButton
        type="button"
        (click)="openProfile.emit(user())"
        i18n-title
        title="View profile"
      >
        <mat-icon>account_circle</mat-icon>
      </button>
    </article>
  `,
})
export class UserArticleComponent {
  user = input.required<User>();
  selected = input<boolean>();

  toggle = output<number>();
  openProfile = output<User>();
}
