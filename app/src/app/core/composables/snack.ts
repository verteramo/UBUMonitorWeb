import { inject } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

export function useSnack(baseConfig?: Partial<MatSnackBarConfig>) {
  const snackBar = inject(MatSnackBar);

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
