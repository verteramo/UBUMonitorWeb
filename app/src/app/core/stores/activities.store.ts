/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CourseService } from '@core/services/course.service';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withSelection } from './features/selection.feature';
import { withStorage } from './features/storage.feature';
import { SessionStore } from './session.store';

/** Propiedades de estado del panel de actividades. */
type ActivitiesState = {
  term: string;
};

/** Estado inicial. */
const initialState: ActivitiesState = {
  term: '',
};

/** Store de las propiedades de estado del panel de usuarios. */
export const ActivitiesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withSelection<number>(),
  withStorage(sessionStorage, 'activities-state'),
  withComputed((store) => {
    const service = inject(CourseService);
    const session = inject(SessionStore);

    const resource = rxResource({
      defaultValue: [],
      params: session.currentCourse,
      stream: ({ params }) => service.getSections(params.id),
    });

    const sections = computed(() => resource.value());

    return {
      isLoading: resource.isLoading,
      sections,

      filteredSections: computed(() => {
        const term = store.term().trim().toLowerCase();

        return sections().filter((section) => {
          const matchesTerm = !term || section.name?.toLowerCase()?.includes(term);

          return matchesTerm;
        });
      }),

      selectedSections: computed(() => {
        return sections().filter(({ id }) => store.selectionSet().has(id));
      }),

      modules: computed(() => {
        const values = sections()
          .flatMap((section) => section.modules)
          .flatMap((module) => module.name)
          .filter(Boolean);
        return [...new Set(values)];
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
    };
  }),
  withMethods((store) => ({
    updateTerm(term: string): void {
      patchState(store, { term });
    },
  })),
);
