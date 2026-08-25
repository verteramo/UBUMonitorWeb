/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs';

/** Interfaz del objeto de error que se maneja dentro de la aplicación. */
export interface AppError {
  status: number;
  message: string;
  instance?: string;
}

/** Mensaje de error de fallback para casos donde no se puede determinar. */
const MESSAGE_FALLBACK = $localize`Unexpected error`;

/** Mapa de mensajes de error normalizados. */
const MESSAGES: Record<number, string> = {
  401: $localize`Session has expired`,
  403: $localize`Invalid login, please try again`,
  502: $localize`Server unreachable`,
};

/** Interceptor que normaliza y relanza los errores. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      console.debug(response);
      /*
       * La propiedad error de HttpErrorResponse es de tipo any,
       * pero el backend devuelve un objeto que cumple con la interfaz
       * ProblemDetail, por lo que el mensaje viene en la propiedad detail.
       * https://datatracker.ietf.org/doc/html/rfc7807/#section-3.1
       *
       * No obstante, se priorizan los mensajes normalizados del mapa MESSAGES.
       */

      const {
        status,
        url,
        error: { detail },
      } = response;

      const appError: AppError = {
        status: status || 502,
        message: MESSAGES[status || 502] || detail || MESSAGE_FALLBACK,
        instance: url ?? undefined,
      };

      // Relanzamiento de error
      throw appError;
    }),
  );
};
