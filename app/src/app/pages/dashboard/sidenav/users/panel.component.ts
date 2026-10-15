import { Component, inject, model } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { Participant } from '@core/models/participant';
import { DatasetStore } from '@core/stores/dataset.store';
import { ExpansionPanelComponent } from '@shared/components/expansion-panel.component';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import { ProfileComponent } from './profile.component';
import { UserComponent } from './user.component';
import { UserStore } from './users.store';

@Component({
  selector: 'app-users-panel',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    ExpansionPanelComponent,
    UserComponent,
    FilterControlComponent,
  ],
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      flex: 1;
      min-height: 0;
      overflow: hidden;
    }
  `,
  template: `
    <app-expansion-panel
      i18n-title
      title="Users"
      [(expanded)]="expanded"
      [loading]="dataset.data.isLoading()"
    >
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
        @for (user of store.filteredItems(); track user.id) {
          <app-user
            [user]="user"
            [selected]="store.isSelected(user)"
            (toggle)="store.toggleItem(user)"
            (openProfile)="openProfile($event)"
          />
        }
      </main>
    </app-expansion-panel>
  `,
  providers: [UserStore],
})
export class UserPanelComponent {
  protected readonly store = inject(UserStore);
  protected readonly dataset = inject(DatasetStore);

  readonly expanded = model.required<boolean>();
  readonly dialog = inject(MatDialog);

  openProfile(user: Participant) {
    this.dialog.open(ProfileComponent, { data: user });
  }
}
