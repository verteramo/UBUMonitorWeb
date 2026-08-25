/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse } from '@angular/common/http';
import { computed, effect, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { AuthService, LoginParams } from '@core/services/auth.service';
import { tapResponse } from '@ngrx/operators';
import {
  patchState,
  signalStore,
  withComputed,
  withHooks,
  withMethods,
  withState,
} from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { exhaustMap, pipe, tap } from 'rxjs';
import { withStorage } from './features/storage.feature';

type LoginPayload = {
  params: LoginParams;
  onSuccess?: () => void;
  onError?: (e: HttpErrorResponse) => void;
};

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
  withComputed((store) => ({
    /** Curso seleccionado. */
    currentCourse: computed(() => store.course() as Course),

    /** Usuario logueado. */
    currentPrincipal: computed(() => store.principal() as Principal),
  })),
  withMethods((store, authService = inject(AuthService)) => ({
    /**
     * Establece el curso.
     *
     * @param course Curso.
     */
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

    /**
     * Inicio de sesión e hidratación del principal del store.
     *
     * @param LoginPayload Datos de inicio de sesión y callbacks de éxito y error.
     */
    login: rxMethod<LoginPayload>(
      pipe(
        exhaustMap(({ params, onSuccess, onError }) =>
          authService.login(params).pipe(
            tapResponse<Principal, HttpErrorResponse>({
              next: (principal) => {
                patchState(store, { principal });
                onSuccess?.();
              },
              error: (e) => {
                console.error(e);
                onError?.(e);
              },
            }),
          ),
        ),
      ),
    ),

    /** Cierre de sesión. */
    logout: rxMethod<void>(
      pipe(
        tap(() => {
          patchState(store, initialState);
          authService.logout();
        }),
      ),
    ),
  })),
  withHooks({
    /*
     * Hook que, de acuerdo con la existencia del principal y del curso,
     * sitúa al usuario inmediatamente en la ruta en la que debe estar.
     *
     * Este efecto no sustituye a las guardas, ya que estas actúan mucho antes
     * durante el proceso de enrutamiento; el efecto garantiza que si se cambia
     * el estado del store, el usuario será redirigido automáticamente.
     */
    onInit: (store, router = inject(Router)) => {
      effect(() => {
        if (!store.principal()) {
          // Si no hay usuario, solo se puede acceder al login
          router.navigate(['/login']);
        } else if (!store.course()) {
          // Si no hay curso, solo se puede puede acceder a la selección de curso
          router.navigate(['/course']);
        } else {
          // En caso de que existan ambos, solo se puede acceder al dashboard
          router.navigate(['/dashboard']);
        }
      });
    },
  }),
);
