/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CourseService } from '@core/services/course.service';
import { signalStore, withComputed, withMethods, withProps, withState } from '@ngrx/signals';
import { withSelection } from './features/selection.feature';
import { withStorage } from './features/storage.feature';
import { SessionStore } from './session.store';

/** Propiedades de estado del panel de usuarios. */
type UsersState = {
  term: string;
  roles: string[];
  groups: string[];
};

/** Estado inicial. */
const initialState: UsersState = {
  term: '',
  roles: [],
  groups: [],
};

/** Store de las propiedades de estado del panel de usuarios. */
export const UsersStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withSelection<number>(),
  withStorage(sessionStorage, 'users-state'),
  withProps((_, session = inject(SessionStore), service = inject(CourseService)) => ({
    _resource: rxResource({
      defaultValue: [],
      params: session.currentCourse,
      stream: ({ params: { id } }) => service.getUsers(id),
    }),
  })),
  withComputed(({ _resource: { value, isLoading } }) => ({ users: value, isLoading })),
  withComputed(({ users, term, roles, groups }) => ({
    filteredUsers: computed(() => {
      const value = term().trim().toLowerCase();

      return users().filter((user) => {
        const matchesTerm = !value || user.fullName.toLowerCase().includes(value);
        const matchesRoles = !roles().length || user.roles.some((e) => roles().includes(e));
        const matchesGroups = !groups().length || user.groups.some((e) => groups().includes(e));

        return matchesTerm && matchesRoles && matchesGroups;
      });
    }),

    availableRoles: computed(() => {
      const values = users()
        .flatMap((user) => user.roles)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    availableGroups: computed(() => {
      const values = users()
        .flatMap((user) => user.groups)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    activeFiltersLength: computed(() => {
      return roles().length + groups().length;
    }),
  })),
  withComputed(({ filteredUsers, _selectionSet }) => ({
    /** Determina si están todos los usuarios seleccionados. */
    isAllSelected: computed(() => {
      return (
        filteredUsers().length > 0 && filteredUsers().every(({ id }) => _selectionSet().has(id))
      );
    }),

    /** Determina si se trata de una selección parcial. */
    isSomeSelected: computed(() => {
      const visible = filteredUsers().filter(({ id }) => _selectionSet().has(id));
      return visible.length > 0 && visible.length < filteredUsers().length;
    }),
  })),
  withMethods((store) => ({
    toggleAll(): void {
      store.toggleItems(store.filteredUsers().map(({ id }) => id));
    },
  })),
);
