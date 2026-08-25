/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Modelo de curso devuelto por el backend. */
export interface Course {
  id: number;
  name: string;
  picture: string;
  starred: boolean;
  startDate: number;
  endDate: number;
  category: string;
}
