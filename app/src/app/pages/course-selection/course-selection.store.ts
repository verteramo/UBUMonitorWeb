import { computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Sort } from '@angular/material/sort';
import { Course } from '@core/models/course';
import { CourseClassification, CourseService } from '@core/services/course.service';
import { withTabs } from '@core/stores/features/tab.feature';
import {
  signalStore,
  withComputed,
  withMethods,
  withProps
} from '@ngrx/signals';
import { of, tap } from 'rxjs';

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
 * Tipo para el modelo del formulario.
 */
type CourseSelectionModel = {
  term: string;
  sorts: Sort[];
  courseId: number | null;
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
  withTabs<CourseClassification>(['all', 'starred', 'recent', 'inprogress', 'future', 'past']),
  withProps(() => ({
    _cache: new Map<CourseClassification, Course[]>(),
  })),
  withProps(({ _cache, tab }, service = inject(CourseService)) => ({
    /**
     * Recurso con los cursos.
     */
    courses: rxResource({
      defaultValue: [],
      params: tab,
      stream: ({ params: tab }) =>
        _cache.has(tab)
          ? of(_cache.get(tab))
          : service.getCourses(tab).pipe(tap((courses) => _cache.set(tab, courses))),
    }),

    /**
     * Modelo del formulario.
     */
    model: signal(initialModelState),
  })),
  withComputed(({ model, tabIndex }) => ({
    /**
     * Estado de la ordenación de las columnas.
     */
    sort: computed(() => model().sorts[tabIndex()]),
  })),
  withComputed(({ courses, model, sort }) => ({
    /**
     * Curso seleccionado.
     */
    selectedCourse: computed(() => {
      const { courseId } = model();
      return courseId ? courses.value()?.find(({ id }) => id === courseId) : undefined;
    }),

    /**
     * Colección de cursos filtrados y ordenados.
     */
    filteredCourses: computed(() => {
      const currentCourses = courses.value() ?? [];
      const term = model().term.trim().toLowerCase();

      const filtered = term
        ? currentCourses.filter(({ name }) => name.toLowerCase().includes(term))
        : [...currentCourses];

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
  withMethods(({ _cache, tabIndex, tab, courses, model }) => ({
    /**
     * Establece el estado de ordenación de una columna determinada.
     *
     * @param newSort Nueva ordenación.
     */
    setSort(newSort: Sort) {
      model.update((state) => ({
        ...state,
        sorts: state.sorts.map((sort, i) => (i === tabIndex() ? newSort : sort)),
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

    refresh(): void {
      _cache.delete(tab());
      courses.reload();
    },
  })),
);
