import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { ProblemDetail } from '@core/models/problem-detail';
import { catchError } from 'rxjs';

/**
 * Interceptor que relanza errores con el objeto ProblemDetail.
 *
 * @param req Solicitud.
 * @param next Siguiente interceptor.
 * @returns El resultado de aplicar el siguiente interceptor sobre la solicitud.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((response: HttpErrorResponse) => {
      /*
       * El objeto error de HttpErrorResponse es de tipo any,
       * pero el backend devuelve un objeto que cumple con la
       * interfaz ProblemDetail.
       */
      console.log(response);

      if (response.status === 502) {
        throw {
          type: response.name,
          title: 'Bad Gateway',
          status: response.status,
          detail: response.message,
          instance: response.url,
        } as ProblemDetail;
      }

      throw response.error;
    }),
  );
};
