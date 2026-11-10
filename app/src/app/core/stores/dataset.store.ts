/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Grade } from '@core/models/grade';
import { LogEntry } from '@core/models/log-entry';
import { Module } from '@core/models/module';
import { Participant } from '@core/models/participant';
import { Section } from '@core/models/section';
import { CourseService } from '@core/services/course.service';
import { signalStore, withMethods, withProps, withState } from '@ngrx/signals';
import { forkJoin, map } from 'rxjs';
import { SessionStore } from './session.store';

/**
 * Propiedades de estado del dataset.
 */
export type DatasetState = {
  participants: Participant[];
  logs: LogEntry[];
  components: string[];
  events: string[];
  sections: Section[];
  modules: Module[];
  grades: Grade[];
};

/**
 * Estado inicial.
 */
const initialState: DatasetState = {
  participants: [],
  logs: [],
  components: [],
  events: [],
  sections: [],
  modules: [],
  grades: [],
};

function flatLogsMetadata(logs: LogEntry[]) {
  const { components, events } = logs.reduce(
    (acc, log) => {
      acc.components.add(log.component);
      acc.events.add(log.event);
      return acc;
    },
    {
      components: new Set<string>(),
      events: new Set<string>(),
    },
  );

  return {
    components: [...components],
    events: [...events],
  };
}

function flatModules(sections: Section[]) {
  return sections.flatMap((section) => section.modules);
}

/**
 * Store que contiene el dataset completo utilizado por la aplicación.
 */
export const DatasetStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withProps(() => {
    const session = inject(SessionStore);
    const service = inject(CourseService);

    return {
      /**
       * Información de usuarios, logs, secciones, calificaciones.
       */
      data: rxResource({
        defaultValue: initialState,
        params: session.currentCourse,
        stream: ({ params: course }) => {
          return (
            course &&
            forkJoin({
              participants: service.getParticipants(course.id),
              logs: service.getLogs(course.id),
              sections: service.getSections(course.id),
              grades: service.getGrades(course.id),
            }).pipe(
              map(({ participants, logs, sections, grades }) => {
                const { components, events } = flatLogsMetadata(logs);
                const modules = flatModules(sections);

                return { participants, logs, components, events, sections, modules, grades };
              }),
            )
          );
        },
      }),
    };
  }),
  withMethods(({ data }) => ({
    /**
     * Refresca el dataset completo desde las fuentes.
     */
    refresh(): void {
      data.reload();
    },
  })),
);
