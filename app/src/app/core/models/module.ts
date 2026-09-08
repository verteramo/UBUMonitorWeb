/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/** Modelo de módulo de sección devuelto por el backend. */
export type Module = {
  id: number;
  url?: string;
  name: string;
  visible: boolean;
  userVisible: boolean;
  type: string;
  picture: string;
  purpose: string;
  plural: string;
  completion: 0 | 1 | 2;
  sinceMs?: number;
  untilMs?: number;
};
