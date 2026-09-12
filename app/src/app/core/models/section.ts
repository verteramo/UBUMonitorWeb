/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Module } from './module';

export type Section = {
  id: number;
  name: string | null;
  order: number | null;
  isVisible: boolean;
  isUserVisible: boolean;
  modules: Module[];
};
