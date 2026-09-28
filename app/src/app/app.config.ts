/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, isDevMode, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';
import { provideServiceWorker } from '@angular/service-worker';
import { authInterceptor } from '@core/interceptors/auth-interceptor';
import { errorInterceptor } from '@core/interceptors/error-interceptor';
import { routes } from './app.routes';

/**
 * Configuración de la aplicación.
 * Aquí se pueden configurar proveedores, estrategías, interceptores, ...
 *
 * @see https://angular.dev/reference/configs/file-structure
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withHashLocation()),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    provideServiceWorker('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000',
    }),
  ],
};
