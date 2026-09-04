/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { useSnack } from '@core/composables/snack';
import { AppError } from '@core/interceptors/error-interceptor';
import { url } from '@core/validators/url-validator';
import { PasswordFieldComponent } from '@shared/components/password-field.component';
import { InputFieldComponent } from '@shared/components/text-field.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';
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
  ],
  providers: [LoginStore],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  /** Snack para notificaciones. */
  private snack = useSnack();

  /** Store del componente. */
  protected readonly store = inject(LoginStore);

  /** Esquema del formulario. */
  protected readonly loginForm = form(this.store.model, (schema) => {
    required(schema.host);
    required(schema.username);
    required(schema.password);
    url(schema.host);
  });

  /** Procesamiento del formulario. */
  protected onSubmit(event: Event) {
    event.preventDefault();

    this.store.login().subscribe({
      error: (e: AppError) => this.snack(e.message),
    });
  }
}
