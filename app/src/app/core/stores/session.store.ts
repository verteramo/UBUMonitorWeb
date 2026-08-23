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

type SessionState = {
  course: Course | null;
  principal: Principal | null;
};

const initialState: SessionState = {
  course: null,
  principal: null,
};

export const SessionStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withStorage(sessionStorage, 'session'),
  withComputed((store) => ({
    currentCourse: computed(() => store.course() as Course),
    currentPrincipal: computed(() => store.principal() as Principal),
  })),
  withMethods((store, authService = inject(AuthService)) => ({
    setCourse: (course: Course) => {
      patchState(store, { course });
    },

    clearCourse: () => {
      patchState(store, { course: null });
    },

    clear: () => {
      patchState(store, initialState);
    },

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

    logout: rxMethod<void>(
      pipe(
        tap(() => {
          patchState(store, initialState);
        }),

        exhaustMap(() => {
          return authService.logout();
        }),
      ),
    ),
  })),
  withHooks({
    onInit: (store, router = inject(Router)) => {
      effect(() => {
        if (!store.principal()) {
          router.navigate(['/login']);
        } else if (!store.course()) {
          router.navigate(['/course-selection']);
        } else {
          router.navigate(['/dashboard']);
        }
      });
    },
  }),
);
