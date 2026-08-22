import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { loginInitialState, LoginState, LoginStore } from '@core/stores/login.store';
import { url } from '@core/validators/url-validator';
import { getState } from '@ngrx/signals';
import { ThemeToggleComponent } from '@shared/components/theme-toggle/theme-toggle.component';

/** Interfaz con los campos del formulario de login. */
export interface LoginForm extends LoginState {
  password: string;
}

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
  private router = inject(Router);
  private loginStore = inject(LoginStore);
  private authService = inject(AuthService);

  /** Señal de visualización del password. */
  $hidePassword = signal(true);

  /** Señal del formulario. */
  $form = signal<LoginForm>({ ...getState(this.loginStore), password: '' });

  /** Señal computada que filtra hosts guardados. */
  $hosts = computed(() => {
    const input = this.$form().host.toLowerCase();
    return this.$form().hosts.filter((host) => host.toLowerCase().includes(input));
  });

  /** Señal computada que verifica el protocolo inseguro. */
  $insecure = computed(() => {
    return this.$form().host.startsWith('http:');
  });

  /** Esquema del formulario. */
  loginForm = form(this.$form, (schema) => {
    url(schema.host, { message: $localize`Invalid host` });
    required(schema.host, { message: $localize`Host required` });
    required(schema.username, { message: $localize`Username required` });
    required(schema.password, { message: $localize`Password required` });
  });

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

    const { host, username, password } = this.$form();

    this.authService.login(host, { username, password }).subscribe({
      next: (principal) => {
        console.log($localize`User authenticated`, principal);
      },
      complete: () => {
        this.loginStore.set(this.$form());
        this.router.navigate(['/course-selection']);
      },
    });
  }

  /** Reestablece los campos con los valores guardados en las preferencias. */
  onRestore() {
    this.$form.set({ ...getState(this.loginStore), password: '' });
  }

  /** Vacía completamente los campos. */
  onClean() {
    this.$form.set({ ...loginInitialState, password: '' });
  }
}
