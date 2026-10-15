/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { environment as common } from './environment.common';

/**
 * Propiedades de entorno específicas para producción.
 */
export const environment = {
  ...common,
  production: true,
};
