import { HttpStatusCode } from '@angular/common/http';
import { AppError } from '@core/interceptors/app-error';
import { Observable, of, OperatorFunction, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

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
