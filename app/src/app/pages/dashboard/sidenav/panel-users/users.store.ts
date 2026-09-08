/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CourseService } from '@core/services/course.service';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSettings } from '@core/stores/features/settings.feature';
import { signalStore, withComputed, withFeature, withProps } from '@ngrx/signals';
import { withSelection } from '../../../../core/stores/features/selection.feature';
import { SessionStore } from '../../../../core/stores/session.store';

/**
 * Propiedades de estado del panel de usuarios.
 */
type UsersState = {
  term: string;
  roles: string[];
  groups: string[];
};

/**
 * Estado inicial.
 */
const initialState: UsersState = {
  term: '',
  roles: [],
  groups: [],
};

/**
 * Store de las propiedades de estado del panel de usuarios.
 */
export const UsersStore = signalStore(
  withProps(
    (_, { currentCourse } = inject(SessionStore), service = inject(CourseService)) => ({
      /**
       * Recurso que obtiene los usuarios del curso seleccionado en la sesión actual.
       */
      users: rxResource({
        defaultValue: [],
        params: currentCourse,
        stream: ({ params }) => params && service.getUsers(params.id),
      }),
    }),
  ),
  withFilters(initialState, ['roles', 'groups']),
  withComputed(({ term, roles, groups }) => ({
    _normTerm: computed(() => term().trim().toLowerCase()),
    _rolesSet: computed(() => new Set(roles())),
    _groupsSet: computed(() => new Set(groups())),
  })),
  withComputed(({ users, _normTerm: term, _rolesSet: roles, _groupsSet: groups }) => ({
    /**
     * Usuarios filtrados por término de búsqueda, roles y grupos.
     */
    filteredUsers: computed(() => {
      return users.value().filter((user) => {
        const matchesTerm = !term() || user.fullName.toLowerCase().includes(term());
        const matchesRoles = !roles().size || user.roles.some((r) => roles().has(r));
        const matchesGroups = !groups().size || user.groups.some((g) => groups().has(g));
        return matchesTerm && matchesRoles && matchesGroups;
      });
    }),

    /**
     * Roles disponibles.
     */
    availableRoles: computed(() => {
      const values = users
        .value()
        .flatMap((user) => user.roles)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    /**
     * Grupos disponibles.
     */
    availableGroups: computed(() => {
      const values = users
        .value()
        .flatMap((user) => user.groups)
        .filter(Boolean);
      return [...new Set(values)];
    }),
  })),
  /**
   * Habilitación de la funcionalidad de selección para el store.
   */
  withFeature(({ filteredUsers }) =>
    withSelection(computed(() => filteredUsers().map(({ id }) => id))),
  ),
  withSettings('users'),
);
