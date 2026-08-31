/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { User } from '@core/models/user';
import { TimeAgoPipe } from '@core/pipes/time-ago.pipe';

/** Componente de visualización del perfil de los usuarios. */
@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    TimeAgoPipe,
  ],
  styles: `
    .profile-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 0;

      .avatar {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        object-fit: cover;
      }

      .user-info {
        display: flex;
        flex-direction: column;
        line-height: 1.2;

        .name {
          font-size: 20px;
        }

        .email {
          font-size: 14px;
          color: var(--mat-sys-on-surface-variant);
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }
      }
    }

    mat-dialog-content {
      min-width: 400px;
      max-width: 480px;
      display: flex;
      flex-direction: column;
    }

    .courses-list {
      background-color: var(--mat-sys-surface-container-lowest);
      border: 1px solid var(--mat-sys-outline-variant);
      border-radius: 8px;
    }
  `,
  template: `
    <div mat-dialog-title class="profile-header">
      <img [src]="user.picture" [alt]="user.fullName" class="avatar" />
      <div class="user-info">
        <span class="name">{{ user.fullName }}</span>
        <a [href]="'mailto:' + user.email" class="email">{{ user.email }}</a>
      </div>
    </div>

    <mat-dialog-content>
      <mat-list>
        <mat-list-item>
          <mat-icon matListItemIcon color="primary">history</mat-icon>
          <span matListItemTitle i18n>First access</span>
          <span matListItemLine>{{ user.firstAccess | timeAgo: 's' }}</span>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="primary">school</mat-icon>
          <span matListItemTitle i18n>Last course access</span>
          <span matListItemLine>{{ user.lastCourseAccess | timeAgo: 's' }}</span>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="primary">login</mat-icon>
          <span matListItemTitle i18n>Last platform access</span>
          <span matListItemLine>{{ user.lastAccess | timeAgo: 's' }}</span>
        </mat-list-item>
      </mat-list>

      @if (user.roles.length || user.groups.length) {
        <mat-chip-set style="margin: 16px 0;">
          @for (role of user.roles; track $index) {
            <mat-chip color="primary">{{ role }}</mat-chip>
          }
          @for (group of user.groups; track $index) {
            <mat-chip>{{ group }}</mat-chip>
          }
        </mat-chip-set>
      }

      <h3 class="mat-subtitle-2" style="margin: 16px 0 8px" i18n>
        Enrollments: {{ user.courses.length || 0 }}
      </h3>
      <mat-list class="courses-list">
        @for (course of user.courses; track $index) {
          <mat-list-item>
            <mat-icon matListItemIcon>book</mat-icon>
            <span matListItemTitle>{{ course }}</span>
          </mat-list-item>
        } @empty {
          <mat-list-item>
            <span
              matListItemTitle
              style="text-align: center; color: var(--mat-sys-on-surface-variant);"
              i18n
              >Not enrollments</span
            >
          </mat-list-item>
        }
      </mat-list>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button matButton matDialogClose i18n>Close</button>
    </mat-dialog-actions>
  `,
})
export class UserDialogComponent {
  user = inject<User>(MAT_DIALOG_DATA);
}
