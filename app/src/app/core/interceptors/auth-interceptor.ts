import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService, LoginRequestToken } from '@core/api/auth.service';
import { catchError } from 'rxjs';

/**
 * Interceptor que verifica la presencia de códigos de estado
 * que evidencian la caducidad del token, por lo que en dichos
 * casos se cierra la sesión.
 *
 * @param req Solicitud.
 * @param next Siguiente interceptor.
 * @returns El resultado de aplicar el siguiente interceptor sobre la solicitud.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const isLoginRequest = req.context.get(LoginRequestToken);

  // Códigos de estado de errores de autenticación
  const sessionExpirationStatuses = [401, 403];

  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      /*
       * Si es una solicitud de login no se intercepta,
       * se lanza el error para que el LoginComponent
       * lo capture y lo pueda mostrar.
       */
      if (!isLoginRequest && sessionExpirationStatuses.includes(response.status)) {
        /*
         * Al hacer el logout ya se encarga el AuthService
         * de limpiar los stores, finalizar la sesión
         * en el servidor y redirigir al LoginComponent.
         */
        authService.logout();
      }

      throw response;
    }),
  );
};
