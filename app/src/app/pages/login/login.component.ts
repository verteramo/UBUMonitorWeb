/*
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
import { AppError } from '@core/interceptors/app-error';
import { url } from '@core/validators/url-validator';
import { PasswordFieldComponent } from '@shared/components/password-field.component';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { InputFieldComponent } from '@shared/components/text-field.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';
import { LoginFormStore } from './login-form.store';

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
  providers: [LoginFormStore],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  private snack = useSnack();
  protected readonly store = inject(LoginFormStore);

  /**
   * Esquema del formulario.
   */
  protected readonly loginForm = form(this.store.model, (schema) => {
    url(schema.host);
    required(schema.username);
    required(schema.password);
  });

  /**
   * Inicia el procedimiento de descubrimiento.
   */
  protected onDiscover(): void {
    this.store.discover().subscribe({ error: (e: AppError) => this.snack(e.message) });
  }

  /**
   * Procesamiento del formulario.
   */
  protected onLogin(): void {
    this.store.login().subscribe({ error: (e: AppError) => this.snack(e.message) });
  }

  /**
   * Manejador del submit del formulario.
   */
  protected onSubmit(event: Event): void {
    event.preventDefault();

    if (this.store.step() == 'host') {
      this.onDiscover();
    } else {
      this.onLogin();
    }
  }
}
