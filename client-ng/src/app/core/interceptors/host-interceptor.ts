import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SESSION_STORAGE } from '@core/services/storage.service';

export const HOST_KEY = 'host';
const HOST_HEADER = 'Moodle-Host';

export const hostInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(SESSION_STORAGE);
  const host = storage.get<string>(HOST_KEY);

  if (host) {
    return next(
      req.clone({
        setHeaders: {
          [HOST_HEADER]: host,
        },
      }),
    );
  }

  return next(req);
};
