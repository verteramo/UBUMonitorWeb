/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthToken } from '@core/services/auth.service';
import { SessionStore } from '@core/stores/session.store';
import { environment as env } from '@env/environment';
import { catchError } from 'rxjs';

/**
 * Realiza un pre y post procesamiento de las solicitudes de autenticación.
 * - Pre: Añade el host en la cabecera correspondiente cuando está disponible.
 * - Post: Verifica la presencia de código de estado que evidencian la caducidad
 * del token para limpiar el store de la sesión local.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const session = inject(SessionStore);
  const token = req.context.get(AuthToken);

  const request = token ? req.clone({ setHeaders: { [env.hostHeader]: token } }) : req;

  return next(request).pipe(
    catchError((response: HttpErrorResponse) => {
      /*
       * Si es una solicitud proveniente de AuthService no se intercepta,
       * se lanza el error para que el LoginComponent lo capture y lo pueda procesar.
       */
      if (!token && [401, 403].includes(response.status)) {
        session.clear();
      }

      throw response;
    }),
  );
};
