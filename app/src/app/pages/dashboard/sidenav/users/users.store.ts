/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { Participant } from '@core/models/participant';
import { withDatasetSlice } from '@core/stores/features/dataset-slice.feature';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSelection } from '@core/stores/features/selection.feature';
import { withSlice } from '@core/stores/features/slices.feature';
import { SessionSettingsStore } from '@core/stores/session-settings.store';
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
  withDatasetSlice('participants'),
  withFeature(({ participants }) =>
    withFilters<Participant, Filters, TransformedFilters>({
      filters: { term: '', roles: [], groups: [] },
      transformFn: ({ term, roles, groups }) => ({
        term: term.trim().toLowerCase(),
        roles: new Set(roles),
        groups: new Set(groups),
      }),
      items: participants,
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
  withComputed(({ participants }) => ({
    /**
     * Roles disponibles.
     */
    availableRoles: computed(() => {
      const values = participants()
        .flatMap((current) => current.roles)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    /**
     * Grupos disponibles.
     */
    availableGroups: computed(() => {
      const values = participants()
        .flatMap((current) => current.groups)
        .filter(Boolean);
      return [...new Set(values)];
    }),
  })),
  /**
   * Habilitación de la funcionalidad de selección para el store.
   */
  withFeature(({ filteredItems }) => withSelection(filteredItems, (user) => user.id)),
  withSlice(SessionSettingsStore, 'users'),
);
