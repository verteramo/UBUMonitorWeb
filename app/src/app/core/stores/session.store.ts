import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

export type SessionState = {
  course: Course | null;
  principal: Principal | null;
};

export const sessionInitialState: SessionState = {
  course: null,
  principal: null,
};

export const SessionStore = signalStore(
  { providedIn: 'root' },
  withState<SessionState>(sessionInitialState),
  withStorage(sessionStorage, 'session-state'),
  withMethods((store) => ({
    setCourse(course: Course | null): void {
      patchState(store, { course });
    },

    setPrincipal(principal: Principal | null): void {
      patchState(store, { principal });
    },

    clearCourse(): void {
      patchState(store, { course: null });
    },

    clearPrincipal(): void {
      patchState(store, { principal: null });
    },

    clearSession(): void {
      patchState(store, sessionInitialState);
    },
  })),
);
