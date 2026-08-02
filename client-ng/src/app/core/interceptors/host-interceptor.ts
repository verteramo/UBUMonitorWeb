import { HttpInterceptorFn } from '@angular/common/http';

export const HOST_KEY = 'login_host';
const HOST_HEADER = 'Moodle-Host';

export const hostInterceptor: HttpInterceptorFn = (req, next) => {
  const host = sessionStorage.getItem(HOST_KEY);

  return host
    ? next(
        req.clone({
          setHeaders: { [HOST_HEADER]: host },
        }),
      )
    : next(req);
};
