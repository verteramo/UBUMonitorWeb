/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, TitleStrategy, withHashLocation } from '@angular/router';
import { authInterceptor } from '@core/interceptors/auth-interceptor';
import { errorInterceptor } from '@core/interceptors/error-interceptor';
import { AppTitleStrategy } from '@core/strategies/app-title.strategy';
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
    { provide: TitleStrategy, useClass: AppTitleStrategy },
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
  ],
};
