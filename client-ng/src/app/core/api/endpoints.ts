import { environment } from '@env/environment';

/**
 * Constante con endpoints preparados para usar globalmente.
 *
 * @author Marcelo Verteramo Pérsico
 */
export const endpoints = {
  login: `${environment.apiUrl}/auth/login`,
  logout: `${environment.apiUrl}/auth/logout`,
  courses: `${environment.apiUrl}/courses`,
  users: `${environment.apiUrl}/users`,
} as const;
