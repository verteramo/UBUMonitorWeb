import { computed, inject, linkedSignal } from '@angular/core';
import { Principal } from '@core/models/principal';
import { AppStore, initialLoginState } from '@core/stores/app.store';
import { SessionStore } from '@core/stores/session.store';
import { signalStore, withComputed, withLinkedState, withMethods, withProps } from '@ngrx/signals';
import { Observable, tap } from 'rxjs';

/*
 * En este fichero se define el estado local del formulario de login.
 */

/** Estado inicial de este store. */
const initialState = { ...initialLoginState, password: '' };

/** Store local para el formulario de login. */
export const LoginStore = signalStore(
  withLinkedState(() => {
    const loadedLoginState = inject(AppStore).login();

    return {
      /** Estado base cargado desde localStorage. */
      loadedState: () => ({ ...initialState, ...loadedLoginState }),

      /** Estado limpio, contiene únicamente la lista de hosts. */
      initialState: () => ({ ...initialState, hosts: loadedLoginState.hosts }),
    };
  }),
  withProps(({ loadedState }) => ({
    /** Modelo subyacente del formulario (recibe las modificaciones del usuario). */
    model: linkedSignal(() => loadedState()),
  })),
  withComputed(({ model }) => ({
    /** Verifica el protocolo inseguro en el campo host. */
    insecure: computed(() => model().host.toLowerCase().startsWith('http:')),

    /** Obtiene la lista de hosts filtrada de acuerdo con la entrada del usuario. */
    filteredHosts: computed(() => {
      const value = model().host.trim().toLowerCase();
      return model().hosts.filter((current) => current.toLowerCase().includes(value));
    }),
  })),
  withMethods(({ model, loadedState, initialState }) => ({
    /** Reestablece el modelo a los valores salvados en localStorage. */
    restore(): void {
      model.set(loadedState());
    },

    /** Reestablece el modelo a los valores vacíos (incluye la lista de hosts). */
    clean(): void {
      model.set(initialState());
    },
  })),
  withMethods(({ model }, session = inject(SessionStore), appStore = inject(AppStore)) => ({
    /** Realiza el login y, en caso de éxito, salva las preferencias seleccionadas. */
    login(): Observable<Principal> {
      const { host, username, password, ...options } = model();

      return session
        .login({ host, credentials: { username, password } })
        .pipe(tap({ next: () => appStore.setLoginState({ host, username, ...options }) }));
    },
  })),
);
