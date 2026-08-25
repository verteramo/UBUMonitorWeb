/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { environment as common } from './environment.common';

/** Propiedades de entorno para desarrollo. */
export const environment = {
  ...common,
  production: false,
};
