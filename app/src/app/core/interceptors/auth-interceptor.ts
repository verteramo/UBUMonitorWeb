import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthRequestToken } from '@core/services/auth.service';
import { SessionStore } from '@core/stores/session.store';
import { catchError } from 'rxjs';

/**
 * Interceptor que verifica la presencia de códigos de estado
 * que evidencian la caducidad del token, por lo que en dichos
 * casos se cierra la sesión.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const sessionStore = inject(SessionStore);
  const isAuthRequest = req.context.get(AuthRequestToken);

  // Códigos de estado de errores de autenticación
  const sessionExpirationStatuses = [401, 403];

  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      /*
       * Si es una solicitud proveniente de AuthService no se intercepta,
       * se lanza el error para que el LoginComponent lo capture y lo pueda mostrar.
       */
      if (!isAuthRequest && sessionExpirationStatuses.includes(response.status)) {
        /*
         * El servidor ha devuelto un código de estado 401/403,
         * por lo que la sesión ya está muerta en el servidor,
         * solo queda limpiar el estado local.
         */
        sessionStore.clear();
      }

      throw response;
    }),
  );
};
