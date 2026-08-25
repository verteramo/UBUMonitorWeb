/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { environment as common } from './environment.common';

/** Propiedades de entorno para producción. */
export const environment = {
  ...common,
  production: true,
};
