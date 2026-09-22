/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { signalStore } from '@ngrx/signals';
import { withSlices } from './features/slices.feature';
import { withStorage } from './features/storage.feature';

/**
 * Store de propiedades de estado de la aplicación (no ligadas a sesión).
 */
export const LocalSettingsStore = signalStore(
  { providedIn: 'root' },
  withSlices(),
  withStorage(localStorage, 'app'),
);
