import { computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { UserService } from '@core/services/user.service';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withSelection } from './features/selection.feature';
import { withStorage } from './features/storage.feature';
import { SessionStore } from './session.store';

type UsersState = {
  term: string;
  roles: string[];
  groups: string[];
};

const initialState: UsersState = {
  term: '',
  roles: [],
  groups: [],
};

export const UsersStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withSelection<number>(),
  withStorage(sessionStorage, 'users-store'),
  withComputed((store, session = inject(SessionStore), service = inject(UserService)) => {
    const resource = rxResource({
      defaultValue: [],
      params: session.currentCourse,
      stream: ({ params }) => service.getUsers(params.id),
    });

    const users = computed(() => resource.value());

    return {
      hasActiveFilters: computed(() => {
        return store.term().length > 0 || store.roles().length > 0 || store.groups().length > 0;
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
        const selection = store.selectionSet();
        return users().filter(({ id }) => selection.has(id));
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
    updateTerm(term: string) {
      patchState(store, { term });
    },

    updateRoles(roles: string[]) {
      patchState(store, { roles });
    },

    updateGroups(groups: string[]) {
      patchState(store, { groups });
    },

    clearFilters() {
      patchState(store, initialState);
    },
  })),
);
