/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { AuthService, LoginParams } from '@core/services/auth.service';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { Observable, tap } from 'rxjs';
import { withStorage } from './features/storage.feature';

/**
 * Propiedades de estado de la sesión.
 */
type SessionState = {
  principal: Principal | null;
  course: Course | null;
};

/**
 * Estado inicial.
 */
const initialState: SessionState = {
  principal: null,
  course: null,
};

/**
 * Store de propiedades de estado de la sesión: Usuario autenticado (Principal) y Curso.
 */
export const SessionStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ principal, course }) => ({
    /**
     * Usuario logueado.
     */
    currentPrincipal: computed(() => principal()!),

    /**
     * Curso seleccionado.
     */
    currentCourse: computed(() => course()!),

    /**
     * Ubicación según el estado de la sesión.
     */
    location: computed(() => (!principal() ? '/login' : !course() ? '/course' : '/dashboard')),
  })),
  withMethods((store, router = inject(Router)) => ({
    /**
     * Establece el principal.
     */
    setPrincipal(principal: Principal): void {
      patchState(store, { principal });
      router.navigate(['/course']);
    },

    /**
     * Establece el curso.
     */
    setCourse(course: Course): void {
      patchState(store, { course });
      router.navigate(['/dashboard']);
    },

    /**
     * Limpia el curso.
     */
    clearCourse(): void {
      patchState(store, { course: null });
      router.navigate(['/course']);
    },

    /**
     * Limpia la sesión completa.
     */
    clear(): void {
      patchState(store, initialState);
      router.navigate(['/login']);
    },
  })),
  withMethods((store, service = inject(AuthService)) => ({
    /**
     * Inicio de sesión e hidratación del principal del store.
     *
     * @param params Datos de inicio de sesión.
     */
    login(params: LoginParams): Observable<Principal> {
      return service.login(params).pipe(
        tap({
          next(principal): void {
            store.setPrincipal(principal);
          },

          error(e): void {
            console.error(e);
          },
        }),
      );
    },

    /**
     * Cierre de sesión.
     */
    logout(): void {
      store.clear();
      service.logout();
    },
  })),
  withStorage(sessionStorage, 'session'),
);
