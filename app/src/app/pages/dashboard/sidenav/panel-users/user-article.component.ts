/*
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

@Component({
  selector: 'app-user-article',
  imports: [MatIconModule, MatCheckboxModule, TimeAgoPipe, MatButtonModule],
  styles: `
    article {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 6px 12px;
      cursor: pointer;
      transition: background-color 0.2s ease;
      border-bottom: 1px solid var(--mat-sys-outline-variant);

      &.selected {
        background-color: var(--mat-sys-primary-container, rgba(0, 0, 0, 0.08));
      }

      &:hover {
        background-color: var(--mat-sys-surface-container-highest, rgba(0, 0, 0, 0.04));
      }

      &.selected,
      &:hover {
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
        width: 32px;
        height: 32px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 32px;
          height: 32px;
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
      gap: 1px;
      flex: 1;
      min-width: 0;

      strong,
      small {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      strong {
        color: var(--mat-sys-on-surface);
        font-weight: 500;
        font-size: 13px;
        line-height: 1.2;
      }

      small {
        color: var(--mat-sys-on-surface-variant, #49454f);
        font-size: 11px;
        line-height: 1.1;
      }
    }

    .profile-btn {
      color: var(--mat-sys-outline, #757575);
      transform: scale(0.9);
    }
  `,
  template: `
    <article [class.selected]="selected()" (click)="toggle.emit(user().id)">
      <div class="avatar-action-container">
        <img [src]="user().picture || 'user_blank.png'" [alt]="user().fullName" />
        <mat-checkbox [checked]="selected()" style="pointer-events: none"></mat-checkbox>
      </div>

      <div class="user-details">
        <strong>{{ user().fullName }}</strong>
        <small i18n>Course: {{ user().lastCourseAccessTs | timeAgo }}</small>
        <small i18n>Platform: {{ user().lastAccessTs | timeAgo }}</small>
      </div>

      <button
        mat-icon-button
        class="profile-btn"
        type="button"
        (click)="onProfileClick($event)"
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

  onProfileClick(event: MouseEvent): void {
    event.stopPropagation();
    this.openProfile.emit(this.user());
  }
}
