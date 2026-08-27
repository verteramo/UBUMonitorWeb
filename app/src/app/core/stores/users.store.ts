/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { UserService } from '@core/services/user.service';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
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
  withComputed((store) => {
    const service = inject(UserService);
    const session = inject(SessionStore);

    const resource = rxResource({
      defaultValue: [],
      params: session.currentCourse,
      stream: ({ params }) => service.getUsers(params.id),
    });

    const users = computed(() => resource.value());

    return {
      activeFiltersCount: computed(() => {
        return store.roles().length + store.groups().length;
      }),

      isLoading: resource.isLoading,
      users,

      filteredUsers: computed(() => {
        const term = store.term().trim().toLowerCase();
        const roles = store.roles();
        const groups = store.groups();

        return users().filter((user) => {
          const matchesTerm = !term || user.fullName.toLowerCase().includes(term);
          const matchesRoles = !roles.length || user.roles.some((e) => roles.includes(e));
          const matchesGroups = !groups.length || user.groups.some((e) => groups.includes(e));

          return matchesTerm && matchesRoles && matchesGroups;
        });
      }),

      selectedUsers: computed(() => {
        return users().filter(({ id }) => store.selectionSet().has(id));
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
    };
  }),
  withMethods((store) => ({
    updateTerm(term: string): void {
      patchState(store, { term });
    },

    updateRoles(roles: string[]): void {
      patchState(store, { roles });
    },

    updateGroups(groups: string[]): void {
      patchState(store, { groups });
    },

    clearFilters(): void {
      patchState(store, { term: '', roles: [], groups: [] });
    },
  })),
);
