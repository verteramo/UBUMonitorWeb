import { HttpInterceptorFn } from '@angular/common/http';
import { HOST_TOKEN } from '@core/api/auth.service';
import { environment } from '@env/environment';

/**
 * Interceptor que añade la cabecera del servidor de Moodle.
 *
 * @param req Solicitud.
 * @param next Siguiente interceptor.
 * @returns El resultado de aplicar el siguiente interceptor sobre la solicitud.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const hostInterceptor: HttpInterceptorFn = (req, next) => {
  const host = req.context.get(HOST_TOKEN);

  if (host) {
    return next(
      req.clone({
        setHeaders: {
          [environment.hostHeader]: host,
        },
      }),
    );
  }

  return next(req);
};
