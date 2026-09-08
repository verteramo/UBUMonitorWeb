/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CourseService } from '@core/services/course.service';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSelection } from '@core/stores/features/selection.feature';
import { withSettings } from '@core/stores/features/settings.feature';
import { SessionStore } from '@core/stores/session.store';
import { signalStore, withComputed, withFeature, withProps } from '@ngrx/signals';

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
  withProps(
    (_, { currentCourse } = inject(SessionStore), service = inject(CourseService)) => ({
      /**
       * Recurso que obtiene las secciones del curso seleccionado en la sesión actual.
       */
      sections: rxResource({
        defaultValue: [],
        params: currentCourse,
        stream: ({ params }) => params && service.getSections(params.id),
      }),
    }),
  ),
  withFilters(initialState),
  withComputed(({ term }) => ({
    _normTerm: computed(() => term().trim().toLowerCase()),
  })),

  withComputed(({ sections, _normTerm: term }) => ({
    filteredSections: computed(() => {
      return sections.value().filter((section) => {
        const matchesTerm = !term() || section.name?.toLowerCase()?.includes(term());

        return matchesTerm;
      });
    }),

    availableTypes: computed(() => {
      const values = sections
        .value()
        .flatMap((section) => section.modules)
        .flatMap((module) => module.plural)
        .filter(Boolean);
      return [...new Set(values)];
    }),

    availablePurposes: computed(() => {
      const values = sections
        .value()
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
  withSettings('activities'),
);
