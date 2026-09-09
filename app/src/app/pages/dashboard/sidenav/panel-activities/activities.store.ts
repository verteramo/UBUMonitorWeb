/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { withDatasetSlice } from '@core/stores/features/dataset-slice.feature';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSelection } from '@core/stores/features/selection.feature';
import { withSettingsSlice } from '@core/stores/features/settings-slice.feature';
import { signalStore, withComputed, withFeature } from '@ngrx/signals';

/**
 * Propiedades de estado del panel de actividades.
 */
type ActivitiesState = {
  term: string;
};

/**
 * Estado inicial.
 */
const initialState: ActivitiesState = {
  term: '',
};

/**
 * Store de las propiedades de estado del panel de usuarios.
 */
export const ActivitiesStore = signalStore(
  withFilters(initialState),
  withComputed(({ term }) => ({
    _normTerm: computed(() => term().trim().toLowerCase()),
  })),
  withDatasetSlice('sections', 'grades', 'events'),
  withComputed(({ sections, _normTerm: term }) => ({
    filteredSections: computed(() => {
      return sections().filter((section) => {
        const matchesTerm = !term() || section.name?.toLowerCase()?.includes(term());

        return matchesTerm;
      });
    }),

    availableTypes: computed(() => {
      const values = sections()
        .flatMap((section) => section.modules)
        .flatMap((module) => module.plural)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    availablePurposes: computed(() => {
      const values = sections()
        .flatMap((section) => section.modules)
        .flatMap((module) => module.purpose)
        .filter(Boolean);
      return [...new Set(values)];
    }),
  })),
  /**
   * Habilitación de la funcionalidad de selección para el store.
   */
  withFeature(({ filteredSections }) =>
    withSelection(computed(() => filteredSections().map(({ id }) => id))),
  ),
  withSettingsSlice('activities'),
);
