/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Modelo de usuario logueado (principal) devuelto por el backend. */
export interface Principal {
  id: string;
  username: string;
  isAdmin: boolean;
  language: string;
  firstName: string;
  lastName: string;
  fullName: string;
  picture?: string;
  platform: {
    url: string;
    name: string;
    version?: string;
    release?: string;
  };
}
