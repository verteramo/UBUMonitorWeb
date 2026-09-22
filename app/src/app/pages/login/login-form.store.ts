import { computed, inject, linkedSignal } from '@angular/core';
import { LoginOptions } from '@core/models/login-options';
import { Principal } from '@core/models/principal';
import { AuthService } from '@core/services/auth.service';
import { DatasetStore } from '@core/stores/dataset.store';
import { SessionStore } from '@core/stores/session.store';
import { resolveRelativeLinks } from '@core/utils/string.utils';
import {
  patchState,
  signalStore,
  withComputed,
  withMethods,
  withProps,
  withState,
} from '@ngrx/signals';
import { finalize, Observable, tap } from 'rxjs';
import { LoginPreferencesStore } from './login-preferences.store';

/**
 * Pasos del formulario de login.
 */
type LoginStep = 'host' | 'credentials';

/**
 * Estado local del formulario de login.
 */
type LoginFormState = {
  step: LoginStep;
  loading: boolean;
  loginOptions: LoginOptions | null;
};

/**
 * Estado inicial del formulario de login.
 */
const initialState: LoginFormState = {
  step: 'host',
  loading: false,
  loginOptions: null,
};

type LoginModelState = {
  host: string;
  username: string;
  password: string;
  rememberHost: boolean;
  rememberUsername: boolean;
};

/**
 * Estado inicial del modelo del formulario de login.
 */
const initialModel: LoginModelState = {
  host: '',
  username: '',
  password: '',
  rememberHost: false,
  rememberUsername: false,
};

/**
 * Store de propiedades de estado del formulario de login.
 */
export const LoginFormStore = signalStore(
  withState(initialState),
  withComputed(() => {
    const prefs = inject(LoginPreferencesStore);

    return {
      /**
       * Estado inicial con campos vacíos.
       */
      initialModel: computed(() => ({ ...initialModel })),

      /**
       * Estado inicial con las preferencias cargadas.
       */
      loadedModel: computed(() => ({
        ...initialModel,
        host: prefs.host(),
        rememberHost: prefs.rememberHost(),
        username: prefs.username(),
        rememberUsername: prefs.rememberUsername(),
      })),
    };
  }),
  withProps(({ loadedModel }) => ({
    /**
     * WritableSignal que sirve como modelo
     * subyacente para la señal del formulario de login.
     */
    model: linkedSignal(() => loadedModel()),
  })),
  withComputed(({ model, loginOptions }) => {
    const prefs = inject(LoginPreferencesStore);
    const cleanHost = computed(() => model().host.trim().toLowerCase());
    const cleanUsername = computed(() => model().username.trim().toLowerCase());

    return {
      /**
       * Determina si el host introducido utiliza el protocolo inseguro.
       */
      insecure: computed(() => cleanHost().startsWith('http:')),

      /**
       * Lista de hosts filtrados de acuerdo con la entrada del usuario.
       */
      filteredHosts: computed(() =>
        prefs.hosts().filter((current) => current.toLowerCase().startsWith(cleanHost())),
      ),

      /**
       * Lista de nombres de usuarios filtrados de acuerdo con la entrada del usuario.
       */
      filteredUsernames: computed(() =>
        prefs.usernames().filter((current) => current.toLowerCase().startsWith(cleanUsername())),
      ),

      parsedLoginInstructions: computed(() => {
        const instructions = loginOptions()?.loginInstructions;
        return instructions && resolveRelativeLinks(model().host.trim(), instructions);
      }),

      parsedMaintenanceMessage: computed(() => {
        const message = loginOptions()?.maintenanceMessage;
        return message && resolveRelativeLinks(model().host.trim(), message);
      }),
    };
  }),
  withMethods(({ model, step, initialModel, loadedModel }) => {
    function updateModel({ host, rememberHost, username, rememberUsername }: LoginModelState) {
      if (step() === 'host') {
        model.update((state) => ({ ...state, host, rememberHost }));
      } else {
        model.update((state) => ({ ...state, username, rememberUsername }));
      }
    }

    return {
      /**
       * Restaura el estado del modelo con las preferencias cargadas,
       * restaura únicamente los campos presentes en el step actual.
       */
      restore(): void {
        updateModel(loadedModel());
      },

      /**
       * Restaura el estado del modelo con campos vacíos,
       * restaura únicamente los campos presentes en el step actual.
       */
      clean(): void {
        updateModel(initialModel());
      },
    };
  }),
  withMethods((store) => {
    const service = inject(AuthService);
    const session = inject(SessionStore);
    const dataset = inject(DatasetStore);
    const prefs = inject(LoginPreferencesStore);

    return {
      /**
       * Vuelve al primer paso,
       * que corresponde a la entrada del host.
       */
      stepBack(): void {
        store.model.update((state) => ({ ...state, password: '' }));
        patchState(store, { loginOptions: null, step: 'host' });
      },

      /**
       * Realiza el descubrimiento de las opciones de login,
       * en caso de éxito se avanza al paso correspondiente a
       * la entrada de credenciales y se salvan las preferencias
       * seleccionadas para el campo del host.
       */
      discover(): Observable<LoginOptions> {
        const { host, rememberHost } = store.model();

        patchState(store, { loading: true });

        return service.discover(host).pipe(
          tap({
            next(loginOptions): void {
              patchState(store, { loginOptions, step: 'credentials' });
              prefs.setHostPreferences(host, rememberHost);
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => patchState(store, { loading: false })),
        );
      },

      /**
       * Realiza el login y, en caso de éxito, establece el Principal,
       * computa el hash para el dataset y salva las preferencias
       * seleccionadas para el campo del nombre de usuario.
       */
      login(): Observable<Principal> {
        const { host, username, password, rememberUsername } = store.model();

        patchState(store, { loading: true });

        return service.login({ host, credentials: { username, password } }).pipe(
          tap({
            next(principal): void {
              session.setPrincipal(principal);
              dataset.computeHash(username, password);
              prefs.saveUsernamePreferences(username, rememberUsername);
            },

            error(e): void {
              console.error(e);
            },
          }),
          finalize(() => patchState(store, { loading: false })),
        );
      },

      loginViaBrowser(url: string): void {
        const host = store.model().host.trim();
        const passport = Math.random().toString(36).substring(2, 15);
        const callbackScheme = 'ubumonitor';
        window.location.href = `${host}/admin/tool/mobile/launch.php?service=moodle_mobile_app&urlscheme=${callbackScheme}&passport=${passport}`;
      },
    };
  }),
);
