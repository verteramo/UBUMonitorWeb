import { inject, Service } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Service()
export class SnackService {
  private snackBar = inject(MatSnackBar);

  show(message: string, action?: string, config?: Partial<MatSnackBarConfig>) {
    this.snackBar.open(message, action, {
      duration: 4000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      ...config,
    });
  }
}
