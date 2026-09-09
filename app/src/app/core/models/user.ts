/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Modelo de usuario de Moodle devuelto por el backend. */
export type User = {
  id: number;
  username: string | null;
  email: string | null;
  fullName: string;
  picture: string | null;
  firstAccessTs: number | null;
  lastAccessTs: number | null;
  lastCourseAccessTs: number | null;
  country: string | null;
  phones: string[];
  groups: string[];
  roles: string[];
  courses: string[];
};
