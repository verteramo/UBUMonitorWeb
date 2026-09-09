/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { User } from '@core/models/user';
import { UsersStore } from '@pages/dashboard/sidenav/panel-users/users.store';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { UserArticleComponent } from './user-article.component';
import { UserDialogComponent } from './user-dialog.component';

/** Componente del panel de usuarios. */
@Component({
  selector: 'app-users-panel',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    UserArticleComponent,
    ProgressSpinnerComponent,
    FilterControlComponent,
  ],
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      flex: 1;
      height: 100%;
      min-height: 0;
      overflow: hidden;
    }

    main {
      flex: 1;
      min-height: 0;
      overflow-y: auto;
    }
  `,
  template: `
    @if (store.isLoading()) {
      <app-progress-spinner i18n>Loading users...</app-progress-spinner>
    } @else {
      <header>
        <app-filter-control
          i18n-placeholder
          placeholder="Filter..."
          [checked]="store.isCompleteSelection()"
          [indeterminate]="store.isPartialSelection()"
          [badge]="store.activeFiltersCount()"
          [term]="store.term()"
          (termChange)="store.setTerm($event)"
          (toggleAll)="store.toggleItems()"
        >
          @if (store.availableRoles()) {
            <mat-form-field appearance="outline" subscriptSizing="dynamic">
              <mat-select
                [value]="store.roles()"
                (valueChange)="store.setRoles($event)"
                multiple
                i18n-placeholder
                placeholder="Roles"
              >
                @for (role of store.availableRoles(); track role) {
                  <mat-option [value]="role">{{ role }}</mat-option>
                }
              </mat-select>
            </mat-form-field>
          }

          @if (store.availableGroups()) {
            <mat-form-field appearance="outline" subscriptSizing="dynamic">
              <mat-select
                [value]="store.groups()"
                (valueChange)="store.setGroups($event)"
                multiple
                i18n-placeholder
                placeholder="Groups"
              >
                @for (group of store.availableGroups(); track group) {
                  <mat-option [value]="group">{{ group }}</mat-option>
                }
              </mat-select>
            </mat-form-field>
          }
        </app-filter-control>
      </header>

      <main>
        @for (user of store.filteredUsers(); track user.id) {
          <app-user-article
            [user]="user"
            [selected]="store.isSelected(user.id)"
            (toggle)="store.toggleItem($event)"
            (openProfile)="openProfile($event)"
          />
        }
      </main>
    }
  `,
  providers: [UsersStore],
})
export class UserPanelComponent {
  readonly dialog = inject(MatDialog);
  readonly store = inject(UsersStore);

  /** Abre el diálogo con el perfil de un usuario. */
  openProfile(user: User) {
    this.dialog.open(UserDialogComponent, { data: user });
  }
}
