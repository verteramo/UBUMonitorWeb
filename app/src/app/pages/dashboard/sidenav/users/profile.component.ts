import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { Participant } from '@core/models/participant';
import { AvatarComponent } from '@shared/components/avatar.component';
import { TimestampComponent } from '@shared/components/timestamp.component';

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    TimestampComponent,
    AvatarComponent,
  ],
  styles: `
    :host {
      display: block;
      background-color: var(--mat-sys-surface-container-high, #f1f0f4);
    }

    /* Perfora la encapsulación para modificar el contenedor nativo de Material */
    ::ng-deep .mdc-dialog__surface {
      border-radius: 12px !important;
      border: 1px solid var(--mat-sys-outline, #79747e) !important;
    }

    .profile-header {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 24px 24px 16px 24px;
      margin-bottom: 0;

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

    .section-title {
      margin: 16px 0 8px;
      color: var(--mat-sys-on-surface);
    }

    .chip-container {
      margin-bottom: 8px;
    }

    .courses-list {
      background-color: var(--mat-sys-surface-container-lowest);
      border: 1px solid var(--mat-sys-outline-variant);
      border-radius: 8px;
    }
  `,
  template: `
    <div mat-dialog-title class="profile-header">
      <app-avatar [data]="user" [size]="64" />
      <div class="user-info">
        <span class="name">{{ user.fullName }}</span>
        <a [href]="'mailto:' + user.email" class="email">{{ user.email }}</a>
      </div>
    </div>

    <mat-dialog-content>
      <mat-list>
        <mat-list-item>
          <mat-icon matListItemIcon color="primary">history</mat-icon>
          <span matListItemTitle i18n>First platform access</span>
          <span matListItemLine>
            <app-timestamp [value]="user.firstAccessTs" />
          </span>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="primary">school</mat-icon>
          <span matListItemTitle i18n>Last course access</span>
          <span matListItemLine>
            <app-timestamp [value]="user.lastCourseAccessTs" />
          </span>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="primary">login</mat-icon>
          <span matListItemTitle i18n>Last platform access</span>
          <span matListItemLine>
            <app-timestamp [value]="user.lastAccessTs" />
          </span>
        </mat-list-item>
      </mat-list>

      @if (user.roles.length) {
        <h3 class="mat-subtitle-2 section-title" i18n>Roles</h3>
        <mat-chip-set class="chip-container">
          @for (role of user.roles; track $index) {
            <mat-chip color="primary">{{ role }}</mat-chip>
          }
        </mat-chip-set>
      }

      @if (user.groups.length) {
        <h3 class="mat-subtitle-2 section-title" i18n>Groups</h3>
        <mat-chip-set class="chip-container">
          @for (group of user.groups; track $index) {
            <mat-chip>{{ group }}</mat-chip>
          }
        </mat-chip-set>
      }

      <h3 class="mat-subtitle-2 section-title" i18n>
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
              >No enrollments</span
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
export class ProfileComponent {
  user = inject<Participant>(MAT_DIALOG_DATA);
}
