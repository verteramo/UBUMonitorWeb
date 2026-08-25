/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Modelo de usuario de Moodle devuelto por el backend. */
export interface User {
  id: number;
  username?: string;
  email?: string;
  fullName: string;
  picture?: string;
  firstAccess?: number;
  lastAccess?: number;
  lastCourseAccess?: number;
  country?: string;
  phones: string[];
  groups: string[];
  roles: string[];
  courses: string[];
}
