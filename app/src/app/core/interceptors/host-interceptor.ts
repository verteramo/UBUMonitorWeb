/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpInterceptorFn } from '@angular/common/http';
import { HostToken } from '@core/services/auth.service';
import { environment as env } from '@env/environment';

/** Interceptor que añade la cabecera con el servidor de Moodle. */
export const hostInterceptor: HttpInterceptorFn = (req, next) => {
  const host = req.context.get(HostToken);

  if (host) {
    return next(req.clone({ setHeaders: { [env.hostHeader]: host } }));
  }

  return next(req);
};
