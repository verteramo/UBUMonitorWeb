/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { AppError } from '@core/models/app-error';
import { catchError } from 'rxjs';

/** Relanza los errores normalizados construyendo un AppError. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      console.debug(response);
      throw new AppError(response);
    }),
  );
};
