import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { ProblemDetail } from '@core/models/problem-detail';
import { SnackService } from '@core/services/snack.service';
import { LoginPrefs, LoginPrefsStore } from '@core/store/login-prefs.store';
import { url } from '@core/validators/url-validator';

export interface LoginForm extends LoginPrefs {
  password: string;
}

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
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  private router = inject(Router);
  private prefsStore = inject(LoginPrefsStore);
  private authService = inject(AuthService);
  private snackService = inject(SnackService);

  /** Señal del formulario. */
  $form = signal<LoginForm>({ ...this.prefsStore.$value(), password: '' });

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
    url(schema.host, { message: $localize`Invalid URL` });
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
        console.log($localize`Authenticated`, principal);
      },
      error: ({ detail }: ProblemDetail) => {
        this.snackService.show(detail);
      },
      complete: () => {
        this.prefsStore.set(this.$form());
        this.router.navigate(['/course-selection']);
      },
    });
  }

  /** Reestablece los campos con los valores guardados en las preferencias. */
  onRefresh() {
    this.$form.set({ ...this.prefsStore.$value(), password: '' });
  }

  /** Vacía completamente los campos. */
  onClear() {
    this.$form.set({ ...this.prefsStore.initialValue, password: '' });
  }
}
