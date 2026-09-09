/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/**
 * Modelo de usuario logueado (principal) devuelto por el backend.
 */
export type Principal = {
  id: string;
  username: string;
  language: string;
  firstName: string;
  lastName: string;
  fullName: string;
  picture: string | null;
  isAdmin: boolean;
  siteUrl: string;
  siteName: string;
  version: string | null;
  release: string | null;
};
