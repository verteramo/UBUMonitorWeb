/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject, OnInit } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { useSnack } from '@core/composables/snack';
import { AppError } from '@core/interceptors/app-error';
import { LoginType } from '@core/models/site-config';
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
export class LoginComponent implements OnInit {
  protected readonly LoginType = LoginType;

  private snack = useSnack();
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  protected readonly store = inject(LoginFormStore);

  /**
   * Modelo del formulario.
   */
  protected readonly loginForm = form(this.store.model, (schema) => {
    url(schema.site);
    required(schema.username);
    required(schema.password);
  });

  /**
   * Montaje del componente, verifica la presencia del parámetro y lanza el
   * procedimiento de login mediante token SSO.
   */
  ngOnInit() {
    const link = this.route.snapshot.queryParamMap.get('link');

    if (link) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { link: null },
        queryParamsHandling: 'merge',
        replaceUrl: true,
      });

      this.store.loginSso(link).subscribe({ error: (e: AppError) => this.snack(e.message) });
    }
  }
  /**
   * Inicia el procedimiento de descubrimiento.
   *
   * @param event Evento.
   */
  protected onDiscover(event: Event): void {
    event.preventDefault();
    this.store.discover().subscribe({ error: (e: AppError) => this.snack(e.message) });
  }

  /**
   * Ejecuta el procedimiento de login mediante credenciales (username/password).
   *
   * @param event Evento.
   */
  protected onLogin(event: Event): void {
    event.preventDefault();
    this.store.login().subscribe({ error: (e: AppError) => this.snack(e.message) });
  }

  /**
   * Inicial el procedimiento de login mediante token SSO.
   *
   * @param event Evento.
   */
  protected onSsoLogin(event: Event): void {
    event.preventDefault();
    this.store.startSsoLogin();
  }
}
