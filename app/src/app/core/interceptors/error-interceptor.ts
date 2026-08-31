/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs';

/** Tipo del objeto de error que se maneja dentro de la aplicación. */
export type AppError = {
  status: number;
  message: string;
  url?: string;
};

/** Mensaje de error de fallback para casos donde no se puede determinar. */
const MESSAGE_FALLBACK = $localize`Unexpected error`;

/** Mapa de mensajes de error normalizados. */
const MESSAGES: Record<number, string> = {
  401: $localize`Session has expired`,
  403: $localize`Invalid login, please try again`,
  502: $localize`Server unreachable`,
};

/**
 * Convierte un error HttpErrorResponse en un error AppError.
 *
 * Si el servidor no responde, HttpErrorResponse se instancia con
 * status === 0, por lo que, a efectos internos, es como un 502 Bad Gateway.
 *
 * La propiedad error es de tipo any, pero como se tiene control del
 * backend, este devuelve un objeto que cumple con la interfaz ProblemDetail:
 * https://datatracker.ietf.org/doc/html/rfc7807/#section-3.1
 */
function normalize({ status, error, url }: HttpErrorResponse): AppError {
  const code = status || 502;

  return {
    status: code,
    message: MESSAGES[code] || error?.detail || MESSAGE_FALLBACK,
    url: url ?? undefined,
  };
}

/** Relanza los errores normalizados. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      console.debug(response);
      throw normalize(response);
    }),
  );
};
