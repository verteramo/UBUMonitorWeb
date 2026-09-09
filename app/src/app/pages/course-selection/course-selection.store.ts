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

/**
 * Clasificaciones de las pestañas del componente.
 */
const classifications: CourseClassification[] = [
  'all',
  'starred',
  'recent',
  'inprogress',
  'future',
  'past',
];

/**
 * Propiedades de estado del componente de selección de curso.
 */
type CourseSelectionState = {
  tab: number;
};

/**
 * Tipo para el modelo del formulario.
 */
type CourseSelectionModel = {
  term: string;
  sorts: Sort[];
  courseId: number | null;
};

/**
 * Estado inicial del componente.
 */
const initialState: CourseSelectionState = {
  tab: 0,
};

/**
 * Estado inicial de la ordenación de las columnas.
 */
const initialSortsState: Sort[] = classifications.map(() => ({ active: '', direction: '' }));

/**
 * Estado inicial del modelo del formulario.
 */
const initialModelState: CourseSelectionModel = {
  term: '',
  sorts: initialSortsState,
  courseId: null,
};

/**
 * Store de propiedades de estado del componente de selección de curso.
 */
export const CourseSelectionStore = signalStore(
  withState(initialState),
  withProps(({ tab }, service = inject(CourseService)) => ({
    /**
     * Recurso con los cursos.
     */
    courses: rxResource({
      defaultValue: [],
      params: () => classifications[tab()],
      stream: ({ params }) => service.getCourses(params),
    }),

    /**
     * Modelo del formulario.
     */
    model: signal(initialModelState),
  })),
  withComputed(({ model, tab }) => ({
    /**
     * Estado de la ordenación de las columnas.
     */
    sort: computed(() => model().sorts[tab()]),
  })),
  withComputed(({ courses, model, sort }) => ({
    /**
     * Curso seleccionado.
     */
    selectedCourse: computed(() => {
      const { courseId } = model();
      return courseId ? courses.value().find(({ id }) => id === courseId) : undefined;
    }),

    /**
     * Colección de cursos filtrados y ordenados.
     */
    filteredCourses: computed(() => {
      const term = model().term.trim().toLowerCase();

      const filtered = term
        ? courses.value().filter(({ name }) => name.toLowerCase().includes(term))
        : [...courses.value()];

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
    /**
     * Establece la pestaña seleccionada.
     *
     * @param tab Pestaña.
     */
    setTab(tab: number): void {
      patchState(store, { tab });
    },
  })),
  withMethods(({ model, tab }) => ({
    /**
     * Establece el estado de ordenación de una columna determinada.
     *
     * @param newSort Nueva ordenación.
     */
    setSort(newSort: Sort) {
      model.update((state) => ({
        ...state,
        sorts: state.sorts.map((sort, i) => (i === tab() ? newSort : sort)),
      }));
    },

    /**
     * Establece el ID del curso seleccionado.
     *
     * @param id ID del curso.
     */
    setCourseId(id: number) {
      model.update((state) => ({ ...state, courseId: id }));
    },
  })),
);
