/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Propiedades de entorno comunes para desarrollo y producción. */
export const environment = {
  hostHeader: 'Moodle-Host',
  endpoints: {
    login: '/api/auth/login',
    logout: '/api/auth/logout',
    courses: '/api/courses',
    users: '/api/users',
  },
};
