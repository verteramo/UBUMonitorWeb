/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { withCollection } from '@core/stores/features/collection.feature';
import { signalStore, withComputed } from '@ngrx/signals';

/**
 * Filtros del panel de usuarios.
 */
type Filters = {
  term: string;
  roles: string[];
  groups: string[];
};

/**
 * Store del panel de usuarios.
 */
export const UserStore = signalStore(
  withCollection({
    selectionKey: 'participants',
    selector: (dataset) => dataset.participants,
    identifier: (participant) => participant.id,
    searchBy: (participant) => participant.fullName,

    filters: { term: '', roles: [], groups: [] } as Filters,
    computationFn: ({ term, roles, groups }) => ({
      term: term.trim().toLowerCase(),
      roles: new Set(roles),
      groups: new Set(groups),
    }),
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
  withComputed(({ items }) => ({
    /**
     * Roles disponibles.
     */
    availableRoles: computed(() => {
      const values = items()
        .flatMap((current) => current.roles)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    /**
     * Grupos disponibles.
     */
    availableGroups: computed(() => {
      const values = items()
        .flatMap((current) => current.groups)
        .filter(Boolean);
      return [...new Set(values)];
    }),
  })),
);
