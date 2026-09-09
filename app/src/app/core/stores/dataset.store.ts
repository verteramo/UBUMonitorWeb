/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Grade } from '@core/models/grade';
import { Section } from '@core/models/section';
import { User } from '@core/models/user';
import { CourseService } from '@core/services/course.service';
import { signalStore, withMethods, withProps } from '@ngrx/signals';
import { forkJoin } from 'rxjs';
import { SessionStore } from './session.store';

/**
 * Propiedades de estado del dataset.
 */
export type DatasetState = {
  users: User[];
  sections: Section[];
  grades: Grade[];
  events: Event[];
};

/**
 * Estado inicial.
 */
const initialState: DatasetState = {
  users: [],
  sections: [],
  grades: [],
  events: [],
};

/**
 * Store que contiene el dataset completo utilizado por la aplicación.
 */
export const DatasetStore = signalStore(
  { providedIn: 'root' },
  withProps((_, { currentCourse } = inject(SessionStore), service = inject(CourseService)) => ({
    data: rxResource({
      defaultValue: initialState,
      params: currentCourse,
      stream: ({ params: course }) => {
        return (
          course &&
          forkJoin({
            users: service.getUsers(course.id),
            sections: service.getSections(course.id),
            grades: service.getGrades(course.id),
            events: service.getEvents(course.id),
          })
        );
      },
    }),
  })),
  withMethods((store) => ({
    refresh() {
      store.data.reload();
    },
  })),
);
