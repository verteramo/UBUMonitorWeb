import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { UserService } from '@core/api/user.service';
import { User } from '@core/models/user';
import { CourseStore } from '@core/store/course.store';
import { TimeAgoPipe } from '@shared/pipes/time-ago.pipe';
import { UserProfileDialogComponent } from './components/user-profile-dialog/user-profile-dialog.component';

export interface Filters {
  roles: string[];
  groups: string[];
  connections: string[];
}

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatCheckboxModule,
    MatDividerModule,
    TimeAgoPipe,
    MatProgressSpinnerModule,
  ],
  templateUrl: 'users-list.component.html',
  styleUrls: ['users-list.component.scss'],
})
export class UsersListComponent {
  private userService = inject(UserService);
  private courseStore = inject(CourseStore);
  dialog = inject(MatDialog);

  $term = signal('');
  $selectedRoles = signal<string[]>([]);
  $selectedGroups = signal<string[]>([]);

  $users = rxResource({
    defaultValue: [],
    params: () => this.courseStore.value,
    stream: ({ params: course }) => this.userService.getUsers(course.id),
  });

  $roles = computed(() => {
    const values = this.$users
      .value()
      .flatMap((user) => user.roles)
      .filter(Boolean);
    return [...new Set(values)];
  });

  $groups = computed(() => {
    const values = this.$users
      .value()
      .flatMap((user) => user.groups)
      .filter(Boolean);
    return [...new Set(values)];
  });

  $filteredUsers = computed(() => {
    const term = this.$term().trim().toLowerCase();
    const users = this.$users.value();
    const selectedRoles = this.$selectedRoles();
    const selectedGroups = this.$selectedGroups();

    let filteredUsers = !term
      ? [...users]
      : users.filter((user) => user.fullname.toLocaleLowerCase().includes(term));

    if (selectedRoles.length) {
      filteredUsers = filteredUsers.filter((user) => {
        return user.roles.some((role) => selectedRoles.includes(role));
      });
    }

    if (selectedGroups.length) {
      filteredUsers = filteredUsers.filter((user) => {
        return user.groups.some((group) => selectedGroups.includes(group));
      });
    }

    return filteredUsers;
  });

  openProfile(user: User): void {
    this.dialog.open(UserProfileDialogComponent, { data: user });
  }

  // Define la señal reactiva almacenando un Set para optimizar las búsquedas
  readonly selectedUserIds = signal<Set<number>>(new Set());

  // Señal computada que se actualizará automáticamente cuando cambie la selección
  readonly hasSelection = computed(() => this.selectedUserIds().size > 0);

  isSelected(user: User): boolean {
    return this.selectedUserIds().has(user.id);
  }

  toggleSelection(user: User): void {
    this.selectedUserIds.update((selected) => {
      // Creamos un nuevo Set para asegurar la inmutabilidad y disparar la reactividad
      const updatedSelection = new Set(selected);

      if (updatedSelection.has(user.id)) {
        updatedSelection.delete(user.id);
      } else {
        updatedSelection.add(user.id);
      }

      return updatedSelection;
    });
  }

  readonly isAllSelected = computed(() => {
    const users = this.$filteredUsers();
    return users.length > 0 && users.every((u) => this.selectedUserIds().has(u.id));
  });

  readonly isPartiallySelected = computed(() => {
    const selectedCount = this.selectedUserIds().size;
    return selectedCount > 0 && !this.isAllSelected();
  });

  toggleAll(): void {
    if (this.isAllSelected()) {
      this.selectedUserIds.set(new Set());
    } else {
      // Solo seleccionamos los usuarios que están visibles bajo el filtro actual
      const allFilteredIds = this.$filteredUsers().map((u) => u.id);
      this.selectedUserIds.set(new Set(allFilteredIds));
    }
  }
}
