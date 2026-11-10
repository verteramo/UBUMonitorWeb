import { computed, inject } from '@angular/core';
import { SiteConfig } from '@core/models/site-config';
import { User } from '@core/models/user';
import { AuthService } from '@core/services/auth.service';
import { DatasetStore } from '@core/stores/dataset.store';
import { SessionStore } from '@core/stores/session.store';
import { SystemPreferencesStore } from '@core/stores/system-preferences.store';
import { resolveRelativeLinks } from '@core/utils/string.utils';
import { environment as env } from '@env/environment';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { finalize, Observable, tap } from 'rxjs';
import { withLoginModel } from './login-model.feature';

/**
 * Estado local del formulario de login.
 */
type LoginFormState = {
  loading: boolean;
  siteConfig: SiteConfig | null;
};

/**
 * Estado inicial del formulario de login.
 */
const initialState: LoginFormState = {
  loading: false,
  siteConfig: null,
};

/**
 * Store de propiedades de estado del formulario de login.
 */
export const LoginFormStore = signalStore(
  withState(initialState),
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
    const prefs = inject(SystemPreferencesStore);
    const service = inject(AuthService);
    const session = inject(SessionStore);
    const dataset = inject(DatasetStore);

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
        patchState(store, { loading: true });

        return service.discover(site).pipe(
          tap({
            next(siteConfig): void {
              prefs.updateSitePreferences(site, rememberSite);
              patchState(store, { siteConfig });
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => patchState(store, { loading: false })),
        );
      },

      /**
       * Realiza el login y, en caso de éxito, establece el usuario autenticado,
       * computa el hash para el dataset y actualiza las preferencias.
       */
      login(): Observable<User> {
        const { site, username, password, rememberUsername } = store.model();

        patchState(store, { loading: true });

        return service.login({ site, payload: { username, password } }).pipe(
          tap({
            next(user): void {
              session.setUser(user);
              dataset.computeHash(`${username}:${password}`);
              prefs.updateUsernamePreferences(username, rememberUsername);
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => patchState(store, { loading: false })),
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

      loginSso(token: string): Observable<User> {
        const { site } = store.model();

        patchState(store, { loading: true });

        return service.loginSso({ site, payload: { token } }).pipe(
          tap({
            next(user): void {
              session.setUser(user);
              dataset.computeHash(token);
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => patchState(store, { loading: false })),
        );
      },
    };
  }),
);
