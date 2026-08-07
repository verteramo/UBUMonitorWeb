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
  private authService = inject(AuthService);
  private prefs = inject(LoginPrefsStore);
  private snackService = inject(SnackService);

  $form = signal<LoginForm>({ ...this.prefs.$value(), password: '' });

  $hosts = computed(() => {
    const input = this.$form().host.toLowerCase();
    return this.$form().hosts.filter((host) => host.toLowerCase().includes(input));
  });

  $insecure = computed(() => {
    return this.$form().host.startsWith('http:');
  });

  loginForm = form(this.$form, (schema) => {
    url(schema.host, { message: $localize`Invalid URL` });
    required(schema.host, { message: $localize`Host required` });
    required(schema.username, { message: $localize`Username required` });
    required(schema.password, { message: $localize`Password required` });
  });

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
        this.prefs.set(this.$form());
        this.router.navigate(['/course-selection']);
      },
    });
  }

  onClear() {
    this.$form.set({ ...this.prefs.initialValue, password: '' });
  }
}
