/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Module } from './module';

/** Modelo de sección de curso devuelto por el backend. */
export type Section = {
  id: number;
  name?: string;
  visible: boolean;
  userVisble: boolean;
  order?: number;
  modules: Module[];
};
