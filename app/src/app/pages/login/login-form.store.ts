import { computed, inject } from '@angular/core';
import { SiteConfig } from '@core/models/site-config';
import { AuthService, LoginResponse } from '@core/services/auth.service';
import { withLoading } from '@core/stores/features/loading.feature';
import { SessionStore } from '@core/stores/session.store';
import { SystemPreferencesStore } from '@core/stores/system-preferences.store';
import { UserPreferencesStore } from '@core/stores/user-preferences.store';
import { resolveRelativeLinks } from '@core/utils/string.utils';
import { environment as env } from '@env/environment';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { finalize, Observable, tap } from 'rxjs';
import { withLoginModel } from './login-model.feature';

/**
 * Estado local del formulario de login.
 */
type LoginFormState = {
  siteConfig: SiteConfig | null;
};

/**
 * Estado inicial del formulario de login.
 */
const initialState: LoginFormState = {
  siteConfig: null,
};

/**
 * Store de propiedades de estado del formulario de login.
 */
export const LoginFormStore = signalStore(
  withState(initialState),
  withLoading(),
  withLoginModel(),
  withComputed(({ model, siteConfig }) => ({
    parsedLoginInstructions: computed(() => {
      const text = siteConfig()?.loginInstructions;
      return text ? resolveRelativeLinks(text, model().site.trim()) : undefined;
    }),

    parsedMaintenanceMessage: computed(() => {
      const text = siteConfig()?.maintenanceMessage;
      return text ? resolveRelativeLinks(text, model().site.trim()) : undefined;
    }),
  })),
  withMethods((store) => {
    const authService = inject(AuthService);
    const sessionStore = inject(SessionStore);
    const userPreferencesStore = inject(UserPreferencesStore);
    const systemPreferencesStore = inject(SystemPreferencesStore);

    return {
      /**
       * Vuelve al primer paso,
       * que corresponde a la entrada de la URL del sitio.
       */
      stepBack(): void {
        store.restoreUsername();
        patchState(store, { siteConfig: null });
      },

      /**
       * Obtiene la configuración del sitio y, en caso de éxito, actualiza las
       * preferencias del sitio y avanza al paso siguiente.
       */
      discover(): Observable<SiteConfig> {
        const { site, rememberSite } = store.model();

        return authService.discover(site).pipe(
          tap(() => store.startLoading()),
          tap({
            next(siteConfig): void {
              systemPreferencesStore.updateSitePreferences(site, rememberSite);
              patchState(store, { siteConfig });
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => store.stopLoading()),
        );
      },

      /**
       * Realiza el login y, en caso de éxito, establece el usuario autenticado,
       * computa el hash para el dataset y actualiza las preferencias.
       */
      login(): Observable<LoginResponse> {
        const { site, username, password, rememberUsername } = store.model();

        return authService.login({ site, payload: { username, password } }).pipe(
          tap(() => store.startLoading()),
          tap({
            next({ user, preferences }): void {
              sessionStore.setUser(user);
              userPreferencesStore.setPreferences(preferences);
              systemPreferencesStore.updateUsernamePreferences(username, rememberUsername);
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => store.stopLoading()),
        );
      },

      startSsoLogin(): void {
        const siteConfig = store.siteConfig();

        if (siteConfig?.loginUrl) {
          // Se añade el scheme para que Moodle haga
          // la redirección a esta misma aplicación
          const loginUrl = `${siteConfig.loginUrl}&urlscheme=${encodeURIComponent(env.scheme)}`;
          window.location.href = loginUrl;
        }
      },

      loginSso(token: string): Observable<LoginResponse> {
        const { site } = store.model();

        return authService.loginSso({ site, payload: { token } }).pipe(
          tap(() => store.startLoading()),
          tap({
            next({ user, preferences }): void {
              sessionStore.setUser(user);
              userPreferencesStore.setPreferences(preferences);
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => store.stopLoading()),
        );
      },
    };
  }),
);
