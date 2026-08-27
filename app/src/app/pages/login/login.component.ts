/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, inject, linkedSignal, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { LoginStore } from '@core/stores/login.store';
import { SessionStore } from '@core/stores/session.store';
import { url } from '@core/validators/url-validator';
import { getState } from '@ngrx/signals';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';

/**
 * Componente del formulario de login.
 * La gestión de las preferencias se realiza mediante el LoginStore
 * (persistencia en localStorage).
 */
@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    FormField,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatAutocompleteModule,
    ThemeToggleComponent,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  /** Store del componente. */
  #store = inject(LoginStore);

  /** Store que expone el método `login`. */
  #session = inject(SessionStore);

  /** Estado base del store al crear el componente. */
  #baseState = signal(getState(this.#store));

  /** Señal del formulario. */
  model = linkedSignal({
    source: this.#baseState,
    computation: (state) => ({ ...state, password: '' }),
  });

  /** Esquema del formulario. */
  loginForm = form(this.model, (schema) => {
    url(schema.host, { message: $localize`Invalid host` });
    required(schema.host, { message: $localize`Host required` });
    required(schema.username, { message: $localize`Username required` });
    required(schema.password, { message: $localize`Password required` });
  });

  /** Señal que filtra hosts guardados. */
  hosts = computed(() => {
    const { host, hosts } = this.model();
    const input = host.toLowerCase();
    return hosts.filter((host) => host.toLowerCase().includes(input));
  });

  /** Señal que verifica el protocolo inseguro. */
  insecure = computed(() => {
    const { host } = this.model();
    return host.startsWith('http:');
  });

  /** Señal de visibilidad del password. */
  passwordVisibility = signal(false);

  /** Señal las propiedades del campo de password según visibilidad. */
  passwordState = computed(() => {
    if (this.passwordVisibility()) {
      return { type: 'text', icon: 'visibility_off', title: $localize`Hide` };
    }

    return { type: 'password', icon: 'visibility', title: $localize`Show` };
  });

  hostErrorMessages = computed(() =>
    this.loginForm
      .host()
      .errors()
      .map((error) => error.message)
      .join(', '),
  );

  usernameErrorMessages = computed(() =>
    this.loginForm
      .username()
      .errors()
      .map((error) => error.message)
      .join(', '),
  );

  passwordErrorMessages = computed(() =>
    this.loginForm
      .password()
      .errors()
      .map((error) => error.message)
      .join(', '),
  );

  /**
   * Evento de envío del formulario.
   * Se realiza la solicitud de login;
   * en caso de éxito se loguea el usuario autenticado y,
   * al completar, se guardan las preferencias según las
   * casillas habilitadas por el usuario, y se redirige al
   * componente de selección de curso/asignatura.
   */
  onSubmit(event: Event) {
    event.preventDefault();

    const { host, username, password, ...options } = this.model();

    this.#session.login({
      params: { host, credentials: { username, password } },
      onSuccess: () => {
        this.#store.set({ host, username, ...options });
      },
    });
  }

  /** Reestablece los campos con los valores guardados en las preferencias. */
  onRestore() {
    this.#baseState.set({ ...getState(this.#store) });
  }

  /** Vacía completamente los campos. */
  onClean() {
    this.#baseState.set({ ...this.#store.initialState });
  }
}
