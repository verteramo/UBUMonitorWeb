/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/**
 * Propiedades de entorno comunes para desarrollo y producción.
 */
export const environment = {
  scheme: 'web+umw',
  siteHeader: 'Moodle-Site',
  endpoints: {
    discover: '/api/auth/discover',
    login: '/api/auth/login',
    loginSso: '/api/auth/login-sso',
    logout: '/api/auth/logout',
    courses: '/api/courses',
  },
};
