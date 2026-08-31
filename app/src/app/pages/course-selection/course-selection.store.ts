import { computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Sort } from '@angular/material/sort';
import { CourseClassification, CourseService } from '@core/services/course.service';
import {
  patchState,
  signalStore,
  withComputed,
  withMethods,
  withProps,
  withState,
} from '@ngrx/signals';

/** Clasificaciones de las pestañas del componente. */
const classifications: CourseClassification[] = [
  'all',
  'starred',
  'recent',
  'inprogress',
  'future',
  'past',
];

type CourseSelectionState = {
  tab: number;
};

type CourseSelectionModel = {
  term: string;
  sorts: Sort[];
  courseId: number | null;
};

const initialState: CourseSelectionState = {
  tab: 0,
};

const initialSortsState: Sort[] = classifications.map(() => ({ active: '', direction: '' }));

const initialModelState: CourseSelectionModel = {
  term: '',
  sorts: initialSortsState,
  courseId: null,
};

export const CourseSelectionStore = signalStore(
  withState(initialState),
  withProps(({ tab }, service = inject(CourseService)) => ({
    resource: rxResource({
      defaultValue: [],
      params: () => classifications[tab()],
      stream: ({ params }) => service.getCourses(params),
    }),

    model: signal(initialModelState),
  })),
  withComputed(({ model, tab }) => ({
    sort: computed(() => model().sorts[tab()]),
  })),
  withComputed(({ resource, model, sort }) => ({
    selectedCourse: computed(() => {
      const { courseId } = model();
      return courseId ? resource.value().find(({ id }) => id === courseId) : undefined;
    }),

    filteredCourses: computed(() => {
      const term = model().term.trim().toLowerCase();

      const filtered = term
        ? resource.value().filter(({ name }) => name.toLowerCase().includes(term))
        : [...resource.value()];

      const { active, direction } = sort();

      if (active && direction) {
        filtered.sort((a, b) => {
          const course_a = active === 'name' ? a.name : a.category;
          const course_b = active === 'name' ? b.name : b.category;
          return course_a.localeCompare(course_b) * (direction === 'asc' ? 1 : -1);
        });
      }

      return filtered;
    }),
  })),
  withMethods((store) => ({
    setTab(tab: number): void {
      patchState(store, { tab });
    },
  })),
  withMethods(({ model, tab }) => ({
    setSort(newSort: Sort) {
      model.update((state) => ({
        ...state,
        sorts: state.sorts.map((sort, i) => (i === tab() ? newSort : sort)),
      }));
    },

    setCourseId(id: number) {
      model.update((state) => ({ ...state, courseId: id }));
    },
  })),
);
