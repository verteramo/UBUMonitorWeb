/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Grade } from '@core/models/grade';
import { LogEntry } from '@core/models/log-entry';
import { Section } from '@core/models/section';
import { User } from '@core/models/user';
import { CourseService } from '@core/services/course.service';
import { decryptAes, encryptAes, sha256 } from '@core/utils/crypto.utils';
import { patchState, signalStore, withMethods, withProps, withState } from '@ngrx/signals';
import { forkJoin } from 'rxjs';
import { SessionStore } from './session.store';

/**
 * Propiedades de estado del dataset.
 */
export type DatasetState = {
  users: User[];
  logs: LogEntry[];
  sections: Section[];
  grades: Grade[];
};

/**
 * Estado inicial.
 */
const initialState: DatasetState = {
  users: [],
  logs: [],
  sections: [],
  grades: [],
};

/**
 * Store que contiene el dataset completo utilizado por la aplicación.
 */
export const DatasetStore = signalStore(
  { providedIn: 'root' },
  withState<{ hash: string }>({
    /**
     * Hash con el que se cifran/descifran ficheros.
     */
    hash: '',
  }),
  withProps((_, { currentCourse } = inject(SessionStore), service = inject(CourseService)) => ({
    /**
     * Información de usuarios, secciones, calificaciones y eventos.
     */
    data: rxResource({
      defaultValue: initialState,
      params: currentCourse,
      stream: ({ params: course }) => {
        return (
          course &&
          forkJoin({
            users: service.getUsers(course.id),
            logs: service.getLogs(course.id),
            sections: service.getSections(course.id),
            grades: service.getGrades(course.id),
          })
        );
      },
    }),
  })),
  withMethods((store) => ({
    /**
     * Computa y almacena el hash.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     */
    computeHash(username: string, password: string) {
      patchState(store, {
        hash: sha256(`${username}:${password}`),
      });
    },
  })),
  withMethods(({ hash, data }) => ({
    /**
     * Refresca el dataset completo desde las fuentes.
     */
    refresh() {
      data.reload();
    },

    /**
     * Cifra el dataset.
     *
     * @returns Dataset cifrado.
     */
    encrypt(): string {
      return encryptAes(JSON.stringify(data.value()), hash());
    },

    /**
     * Descifra el dataset.
     *
     * @param encryptedData Dataset cifrado.
     */
    decrypt(encryptedData: string): void {
      data.value.set(JSON.parse(decryptAes(encryptedData, hash())));
    },
  })),
);
