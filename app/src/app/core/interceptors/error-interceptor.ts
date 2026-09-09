/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { AppError } from '@core/interceptors/app-error';
import { catchError } from 'rxjs';

/**
 * Se encuentra en la última capa del modelo onion y su tarea es, al
 * recepcionar respuestas del backend, relanzar los errores normalizados
 * construyendo objetos `AppError`, que la aplicación puede manejar de
 * manera más cómoda.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      console.debug(response);
      throw new AppError(response);
    }),
  );
};
