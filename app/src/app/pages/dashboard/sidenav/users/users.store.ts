/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { User } from '@core/models/user';
import { withDatasetSlice } from '@core/stores/features/dataset-slice.feature';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSelection } from '@core/stores/features/selection.feature';
import { withSettingsSlice } from '@core/stores/features/settings-slice.feature';
import { signalStore, withComputed, withFeature } from '@ngrx/signals';

/**
 * Propiedades de estado del panel de usuarios.
 */
type Filters = {
  term: string;
  roles: string[];
  groups: string[];
};

type TransformedFilters = {
  term: string;
  roles: Set<string>;
  groups: Set<string>;
};

/**
 * Store de las propiedades de estado del panel de usuarios.
 */
export const UsersStore = signalStore(
  withDatasetSlice('users'),
  withFeature(({ users }) =>
    withFilters<User, Filters, TransformedFilters>({
      filters: { term: '', roles: [], groups: [] },
      transformFn: ({ term, roles, groups }) => ({
        term: term.trim().toLowerCase(),
        roles: new Set(roles),
        groups: new Set(groups),
      }),
      items: users,
      filterFn: ({ term, roles, groups }, user) => {
        return (
          (!term || user.fullName.toLowerCase().includes(term)) &&
          (!roles.size || user.roles.some((role) => roles.has(role))) &&
          (!groups.size || user.groups.some((group) => groups.has(group)))
        );
      },
      sortFn: (a, b) => {
        return a.fullName.localeCompare(b.fullName);
      },
      countableFilters: ['roles', 'groups'],
    }),
  ),
  withComputed(({ users }) => ({
    /**
     * Roles disponibles.
     */
    availableRoles: computed(() => {
      const values = users()
        .flatMap((user) => user.roles)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    /**
     * Grupos disponibles.
     */
    availableGroups: computed(() => {
      const values = users()
        .flatMap((user) => user.groups)
        .filter(Boolean);
      return [...new Set(values)];
    }),
  })),
  /**
   * Habilitación de la funcionalidad de selección para el store.
   */
  withFeature(({ filteredItems }) => withSelection(filteredItems, (user) => user.id)),
  withSettingsSlice('users'),
);
