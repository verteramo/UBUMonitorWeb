/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { signalStore } from '@ngrx/signals';
import { withRegistry } from './features/registry.feature';
import { withStorage } from './features/storage.feature';

/**
 * Store de propiedades de estado de la aplicación (no ligadas a sesión).
 */
export const GlobalStore = signalStore(
  { providedIn: 'root' },
  withRegistry(),
  withStorage(localStorage, 'global'),
);
