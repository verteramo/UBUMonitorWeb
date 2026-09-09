/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { useSnack } from '@core/composables/snack';
import { AppError } from '@core/interceptors/app-error';
import { AuthToken } from '@core/services/auth.service';
import { SessionStore } from '@core/stores/session.store';
import { environment as env } from '@env/environment';
import { catchError, EMPTY } from 'rxjs';

/**
 * Realiza un pre y post procesamiento de la solicitud de login.
 * - Pre: Añade el host en la cabecera correspondiente cuando este
 *        está disponible mediante el token `AuthToken`.
 *
 * - Post: Verifica la presencia del código de estado `403`, que junto
 *         con la ausencia del token `AuthToken` evidencia la caducidad
 *         del token de Moodle o de la cookie de sesión del backend,
 *         por lo que solo queda limpiar la sesión y volver a requerir
 *         el inicio de sesión.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const session = inject(SessionStore);
  const token = req.context.get(AuthToken);
  const snack = useSnack();

  const request = token ? req.clone({ setHeaders: { [env.hostHeader]: token } }) : req;

  return next(request).pipe(
    // En este momento el error es de tipo AppError porque en la capa más
    // externa del modelo onion se encuentra el error-interceptor,
    // que convierte errores HttpErrorResponse en AppError.
    catchError((error: AppError) => {
      if (!token && error.status === 403) {
        session.clear();
        snack(error.message);

        return EMPTY;
      }

      throw error;
    }),
  );
};
