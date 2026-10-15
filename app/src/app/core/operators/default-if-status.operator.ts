/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpStatusCode } from '@angular/common/http';
import { AppError } from '@core/interceptors/app-error';
import { Observable, of, OperatorFunction, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

/**
 * Operador de RxJS que intercepta errores y emite un valor por defecto
 * si el error coincide con un código de estado HTTP específico.
 *
 * @template T Tipo de los datos emitidos por el observable origen.
 * @template R Tipo del valor por defecto devuelto en caso de coincidir el estado.
 * @param status Código de estado HTTP (`HttpStatusCode`) que activará el valor de respaldo.
 * @param value Valor alternativo que se emitirá si el flujo falla con el error esperado.
 * @returns Un nuevo Observable que emite los datos originales o el valor por defecto ante el error filtrado.
 */
export function defaultIfStatus<T, R>(
  status: HttpStatusCode,
  value: R,
): OperatorFunction<T, T | R> {
  return (source: Observable<T>) =>
    source.pipe(
      catchError((error: unknown) => {
        if (error instanceof AppError && error.status === status) {
          return of(value);
        }

        return throwError(() => error);
      }),
    );
}
