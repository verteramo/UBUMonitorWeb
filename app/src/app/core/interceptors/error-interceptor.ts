import { HttpContextToken, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { useSnack } from '@core/composables/snack';
import { catchError } from 'rxjs';

/**
 * Interfaz del objeto de error que se maneja dentro de la aplicación.
 *
 * https://datatracker.ietf.org/doc/html/rfc7807/
 *
 * @author Marcelo Verteramo Pérsico
 */
export interface AppError {
  status: number;
  message: string;
  instance?: string;
}

/** Token para anular la notificación del error. */
export const SKIP_NOTIFICATION = new HttpContextToken(() => false);

const MESSAGES: { [key: number]: string } = {
  401: $localize`Session has expired`,
  403: $localize`Invalid login, please try again`,
  502: $localize`Server unreachable`,
};

/**
 * Interceptor que relanza errores con el objeto ProblemDetail.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  /** Servicio de notificaciones. */
  const snack = useSnack();

  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      console.debug(response);
      /*
       * El objeto error de HttpErrorResponse es de tipo any,
       * pero el backend devuelve un objeto que cumple con la
       * interfaz ProblemDetail.
       *
       * https://datatracker.ietf.org/doc/html/rfc7807/
       */

      // Conversión del HttpErrorResponse a AppError
      const {
        status,
        url,
        error: { detail },
      } = response;

      const error: AppError = {
        status: status || 502,
        message: MESSAGES[status || 502] || detail || $localize`Unexpected error.`,
        instance: url ?? undefined,
      };

      // Notificación del error si no se omite
      if (!req.context.get(SKIP_NOTIFICATION)) {
        snack(error.message);
      }

      // Relanzamiento de error para los consumidores
      throw error;
    }),
  );
};
