/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { useSnack } from '@core/composables/snack';
import { AppError } from '@core/interceptors/app-error';
import { DatasetStore } from '@core/stores/dataset.store';
import { url } from '@core/validators/url-validator';
import { PasswordFieldComponent } from '@shared/components/password-field.component';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { InputFieldComponent } from '@shared/components/text-field.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';
import { finalize } from 'rxjs';
import { LoginStore } from './login.store';

/**
 * Componente del formulario de login.
 * La gestión de las preferencias se realiza mediante el LoginStore.
 */
@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    FormField,
    MatFormFieldModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    ThemeToggleComponent,
    InputFieldComponent,
    PasswordFieldComponent,
    ProgressSpinnerComponent,
  ],
  providers: [LoginStore],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  /** Snack para notificaciones. */
  private snack = useSnack();

  private readonly dataset = inject(DatasetStore);

  /** Store del componente. */
  protected readonly store = inject(LoginStore);

  /** Esquema del formulario. */
  protected readonly loginForm = form(this.store.model, (schema) => {
    required(schema.host);
    required(schema.username);
    required(schema.password);
    url(schema.host);
  });

  protected readonly isLoading = signal(false);

  /** Procesamiento del formulario. */
  protected onSubmit(event: Event) {
    event.preventDefault();
    this.isLoading.set(true);

    this.store
      .login()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        error: (e: AppError) => this.snack(e.message),
        complete: () => {
          const { username, password } = this.store.model();
          this.dataset.computeHash(username, password);
        },
      });
  }
}
