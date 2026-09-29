/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, inject, OnInit } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute } from '@angular/router';
import { useSnack } from '@core/composables/snack';
import { AppError } from '@core/interceptors/app-error';
import { LoginType } from '@core/models/login-options';
import { withStorage } from '@core/stores/features/storage.feature';
import { cleanHost, parseDeepLink } from '@core/utils/string.utils';
import { url } from '@core/validators/url-validator';
import { environment as env } from '@env/environment';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { PasswordFieldComponent } from '@shared/components/password-field.component';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { InputFieldComponent } from '@shared/components/text-field.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';
import { MD5 } from 'crypto-js';
import { LoginFormStore } from './login-form.store';

type PassportState = {
  host: string | null;
  uuid: string | null;
};

const PassportStore = signalStore(
  withState<PassportState>({ host: null, uuid: null }),
  withStorage(localStorage, 'passport'),
  withComputed(({ host, uuid }) => ({
    /**
     * Cálculo del hash correspondiente a la combinación Host + UUID.
     *
     * https://github.com/moodle/moodle/blob/85af0b5dc354bf03c73db60079c232f004e01433/public/admin/tool/mobile/launch.php#L100
     */
    hash: computed(() => {
      const hostValue = host();
      const uuidValue = uuid();

      if (hostValue && uuidValue) {
        return MD5(cleanHost(hostValue) + uuidValue).toString();
      }

      return null;
    }),
  })),
  withMethods((store) => ({
    startSsoLogin(host: string): string {
      const uuid = crypto.randomUUID();

      patchState(store, { host, uuid });

      return uuid;
    },

    checkHash(hash: string): Boolean {
      return hash === store.hash();
    },

    clear(): void {
      patchState(store, { host: null, uuid: null });
    },
  })),
);

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
  providers: [LoginFormStore, PassportStore],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  protected readonly LoginType = LoginType;

  private snack = useSnack();
  private route = inject(ActivatedRoute);
  private passportStore = inject(PassportStore);
  protected readonly store = inject(LoginFormStore);

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      const deepLink = params['deepLink'];

      if (deepLink) {
        this.onSsoLoginCallback(deepLink);
      }
    });
  }

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

  protected onSsoLogin(event: Event): void {
    event.preventDefault();
    const uuid = this.passportStore.startSsoLogin(this.store.model().host);
    const loginUrl = this.store.loginOptions()?.loginUrl;
    const finalUrl = `${loginUrl}?service=moodle_mobile_app&passport=${uuid}&urlscheme=${env.scheme}`;

    window.location.href = finalUrl;
  }

  private onSsoLoginCallback(deepLink: string): void {
    const host = this.passportStore.host();
    const { hash, ...payload } = parseDeepLink(deepLink);

    if (!host || !this.passportStore.checkHash(hash)) {
      throw new Error('Se ha intentado iniciar sesión mediante SSO sin haberlo solicitado');
    }

    this.store.loginSso(host, payload).subscribe({ error: (e: AppError) => this.snack(e.message) });
  }
}
