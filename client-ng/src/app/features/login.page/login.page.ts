import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';
import { LoginPreferences, PREFERENCES_KEY } from '../../core/models/login-preferences';
import { form, FormField, minLength } from '@angular/forms/signals';
import { HOST_KEY } from '../../core/interceptors/host-interceptor';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { url } from '../../core/validators/url-validator';
import { ProblemDetail } from '../../core/models/problem-detail';
import { MoodlePrincipal } from '../../core/models/moodle-principal';
import { MatSnackBar } from '@angular/material/snack-bar';

interface LoginFormData {
  host: string;
  username: string;
  password: string;
  rememberHost: boolean;
  rememberUser: boolean;
  offlineMode: boolean;
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
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  hosts = signal<string[]>([]);

  loginModel = signal<LoginFormData>({
    host: '',
    username: '',
    password: '',
    rememberHost: false,
    rememberUser: false,
    offlineMode: false,
  });

  loginForm = form(this.loginModel, (schemaPath) => {
    url(schemaPath.host, { message: 'Introduce una URL válida' });
    minLength(schemaPath.username, 1, { message: 'Introduce un nombre de usuario' });
    minLength(schemaPath.password, 1, { message: 'Introduce una contraseña' });
  });

  filteredHosts = computed(() => {
    const inputHost = this.loginModel().host.toLowerCase();
    return this.hosts().filter((host) => host.toLowerCase().includes(inputHost));
  });

  isInsecureHost = computed(() => {
    return this.loginModel().host.startsWith('http:');
  });

  ngOnInit() {
    const savedPrefs = localStorage.getItem(PREFERENCES_KEY);
    if (savedPrefs) {
      const prefs: LoginPreferences = JSON.parse(savedPrefs);
      this.hosts.set(prefs.hosts || []);

      this.loginModel.update((model) => ({
        ...model,
        host: prefs.rememberHost ? prefs.host || '' : '',
        username: prefs.rememberUser ? prefs.username || '' : '',
        rememberHost: prefs.rememberHost || false,
        rememberUser: prefs.rememberUser || false,
      }));
    }
  }

  onSubmit(event: Event) {
    event.preventDefault();

    const { host, username, password } = this.loginModel();

    // Se establece el host de la sesión
    // para que el interceptor lo use en las peticiones HTTP
    sessionStorage.setItem(HOST_KEY, host);

    this.authService.login(username, password).subscribe({
      next: (principal: MoodlePrincipal) => {
        console.log('Autenticado', principal);
        this.saveSuccessfulLogin();
      },
      error: (problem: ProblemDetail) => {
        const message = problem.detail || 'Error desconocido';
        this.snackBar.open(message, 'Cerrar', {
          duration: 4000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
        });
      },
    });
  }

  private saveSuccessfulLogin() {
    const loginData = this.loginModel();

    // Actualización de la lista de hosts conocidos
    const hosts = Array.from(new Set([loginData.host, ...this.hosts()]));

    // Actualización de las preferencias
    const prefs: LoginPreferences = {
      host: loginData.rememberHost ? loginData.host : '',
      username: loginData.rememberUser ? loginData.username : '',
      rememberHost: loginData.rememberHost,
      rememberUser: loginData.rememberUser,
      hosts: hosts,
    };

    this.hosts.set(hosts);
    localStorage.setItem(PREFERENCES_KEY, JSON.stringify(prefs));
  }

  onClear() {
    this.loginModel.set({
      host: '',
      username: '',
      password: '',
      rememberHost: false,
      rememberUser: false,
      offlineMode: false,
    });
  }
}
