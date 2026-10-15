/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

/**
 * Composición configurable para obtener la funcionalidad de un Snack Bar.
 *
 * @param baseConfig Configuración base del SnackBar.
 * @returns Función para mostrar un Snack Bar.
 */
export function useSnack(baseConfig?: Partial<MatSnackBarConfig>) {
  const snackBar = inject(MatSnackBar);

  /**
   * Muestra una notificación emergente (SnackBar).
   * Por defecto tiene una duración de 5 segundos y se muestra en la parte inferior central.
   *
   * @param message Texto de la notificación.
   * @param action Texto opcional para el botón de acción.
   * @param overrideConfig Configuración puntual que sobrescribe la base para esta llamada específica.
   */
  return (message: string, action?: string, overrideConfig?: Partial<MatSnackBarConfig>) => {
    snackBar.open(message, action, {
      duration: 5000,
      verticalPosition: 'bottom',
      horizontalPosition: 'center',
      ...baseConfig,
      ...overrideConfig,
    });
  };
}
