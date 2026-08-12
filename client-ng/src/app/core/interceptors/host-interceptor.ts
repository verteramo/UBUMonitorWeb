import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { HostStore } from '@core/store/host.store';

const HOST_HEADER = 'Moodle-Host';

export const hostInterceptor: HttpInterceptorFn = (req, next) => {
  const hostStore = inject(HostStore);
  const host = hostStore.$value();

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
