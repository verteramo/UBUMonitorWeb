/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, effect, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { AuthService, LoginParams } from '@core/services/auth.service';
import {
  patchState,
  signalStore,
  withComputed,
  withHooks,
  withMethods,
  withState,
} from '@ngrx/signals';
import { Observable, tap } from 'rxjs';
import { withStorage } from './features/storage.feature';

/** Propiedades de estado de la sesión. */
type SessionState = {
  /** Curso actual. */
  course: Course | null;

  /** Usuario autenticado. */
  principal: Principal | null;
};

/** Estado inicial. */
const initialState: SessionState = {
  course: null,
  principal: null,
};

/** Store de propiedades de estado de la sesión: Usuario autenticado (principal) y Curso. */
export const SessionStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withStorage(sessionStorage, 'session-state'),
  withComputed(({ principal, course }) => ({
    /** Usuario logueado. */
    currentPrincipal: computed(() => principal()!),

    /** Curso seleccionado. */
    currentCourse: computed(() => course()!),

    /** Ubicación según el estado de la sesión. */
    targetRoute: computed(() => (!principal() ? '/login' : !course() ? '/course' : '/dashboard')),
  })),
  withMethods((store) => ({
    _setPrincipal(principal: Principal) {
      patchState(store, { principal });
    },

    /** Establece el curso. */
    setCourse(course: Course): void {
      patchState(store, { course });
    },

    /** Limpia el curso. */
    clearCourse(): void {
      patchState(store, { course: null });
    },

    /** Limpia la sesión completa. */
    clear(): void {
      patchState(store, initialState);
    },
  })),
  withMethods(({ _setPrincipal, clear }, service = inject(AuthService)) => ({
    /**
     * Inicio de sesión e hidratación del principal del store.
     *
     * @param params Datos de inicio de sesión.
     */
    login(params: LoginParams): Observable<Principal> {
      return service.login(params).pipe(tap({ next: _setPrincipal, error: console.error }));
    },

    /** Cierre de sesión. */
    logout(): void {
      clear();
      service.logout();
    },
  })),
  withHooks(({ targetRoute }, router = inject(Router)) => ({
    /*
     * Hook que, de acuerdo con la existencia del principal y del curso,
     * sitúa al usuario inmediatamente en la ruta en la que debe estar.
     *
     * Este efecto no sustituye a las guardas, ya que estas actúan mucho antes
     * durante el proceso de enrutamiento; el efecto garantiza que si se cambia
     * el estado del store, el usuario será redirigido automáticamente.
     */
    onInit(): void {
      effect(() => router.navigate([targetRoute()]));
    },
  })),
);
