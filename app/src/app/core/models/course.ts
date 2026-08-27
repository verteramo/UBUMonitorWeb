/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Modelo de curso devuelto por el backend. */
export type Course = {
  id: number;
  name: string;
  picture: string;
  starred: boolean;
  since: number;
  until: number;
  category: string;
};
