import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  loginFormInitialState,
  LoginFormState,
  LoginFormStore,
} from '@core/stores/login-form.store';
import { SessionStore } from '@core/stores/session.store';
import { url } from '@core/validators/url-validator';
import { getState } from '@ngrx/signals';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';

/** Tipo con los campos del formulario de login. */
type LoginModel = LoginFormState & {
  password: string;
};

/**
 * Componente del formulario de login.
 *
 * Se guardan sus preferencias en Local Storage.
 *
 * @author Marcelo Verteramo Pérsico
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
  private loginStore = inject(LoginFormStore);
  private sessionStore = inject(SessionStore);

  /** Señal del formulario. */
  loginModel = signal<LoginModel>({ ...getState(this.loginStore), password: '' });

  /** Esquema del formulario. */
  loginForm = form(this.loginModel, (schema) => {
    url(schema.host, { message: $localize`Invalid host` });
    required(schema.host, { message: $localize`Host required` });
    required(schema.username, { message: $localize`Username required` });
    required(schema.password, { message: $localize`Password required` });
  });

  /** Señal computada que filtra hosts guardados. */
  hosts = computed(() => {
    const input = this.loginModel().host.toLowerCase();
    return this.loginModel().hosts.filter((host) => host.toLowerCase().includes(input));
  });

  /** Señal computada que verifica el protocolo inseguro. */
  insecure = computed(() => this.loginModel().host.startsWith('http:'));

  /** Señal de visibilidad del password. */
  passwordVisibility = signal(false);

  passwordState = computed(() => {
    const visibility = this.passwordVisibility();

    return {
      type: visibility ? 'text' : 'password',
      title: visibility ? $localize`Hide` : $localize`Show`,
      icon: visibility ? 'visibility_off' : 'visibility',
    };
  });

  togglePasswordVisibility() {
    this.passwordVisibility.update((state) => !state);
  }

  hostErrorMessages = computed(() =>
    this.loginForm.host().errors().map((error) => error.message).join(', ')
  );

  usernameErrorMessages = computed(() =>
    this.loginForm.username().errors().map((error) => error.message).join(', ')
  );

  passwordErrorMessages = computed(() =>
    this.loginForm.password().errors().map((error) => error.message).join(', ')
  );

  /**
   * Evento de envío del formulario.
   * Se realiza la solicitud de login,
   * en caso de éxito se loguea el usuario autenticado y,
   * al completar, se guardan las preferencias según las
   * casillas habilitadas por el usuario y se redirige al
   * componente de selección de curso/asignatura.
   */
  onSubmit(event: Event) {
    event.preventDefault();

    const { host, username, password, ...options } = this.loginModel();

    this.sessionStore.login({
      params: { host, credentials: { username, password } },
      onSuccess: () => {
        this.loginStore.set({ host, username, ...options });
      },
    });
  }

  /** Reestablece los campos con los valores guardados en las preferencias. */
  onRestore() {
    this.loginModel.set({ ...getState(this.loginStore), password: '' });
  }

  /** Vacía completamente los campos. */
  onClean() {
    this.loginModel.set({ ...loginFormInitialState, password: '' });
  }
}
