/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, input, model, output, signal } from '@angular/core';
import { FormValueControl, ValidationError, WithOptionalFieldTree } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

/**
 * Componente reutilizable de campo de contraseña con soporte para alternar su visibilidad y validación.
 */
@Component({
  selector: 'app-password-field',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
  styles: `
    mat-form-field {
      width: 100%;
    }

    input::-ms-reveal,
    input::-ms-clear {
      display: none;
    }
  `,
  template: `
    <mat-form-field>
      <mat-label>{{ label() }}</mat-label>
      <input
        matInput
        [type]="state().type"
        [value]="value()"
        (input)="value.set($event.target.value)"
        (blur)="touch.emit()"
        autocomplete="current-password"
      />
      <button
        matSuffix
        matIconButton
        type="button"
        (click)="onIconClick($event)"
        [attr.aria-label]="state().title"
        [attr.aria-pressed]="visibility()"
      >
        <mat-icon [title]="state().title">{{ state().icon }}</mat-icon>
      </button>
      @if (invalid() && touched()) {
        <mat-error>{{ errors()[0].message }}</mat-error>
      }
    </mat-form-field>
  `,
})
export class PasswordFieldComponent implements FormValueControl<string> {
  /**
   * Valor actual de la contraseña.
   */
  readonly value = model('');

  /**
   * Etiqueta descriptiva del campo de contraseña.
   */
  readonly label = input.required<string>();

  /* Gestión de errores */

  /**
   * Evento emitido cuando el componente pierde el foco.
   */
  readonly touch = output<void>();

  /**
   * Indica si el valor actual no es válido.
   */
  readonly invalid = input<boolean>(false);

  /**
   * Indica si el campo ha interactuado con el usuario (estado touched).
   */
  readonly touched = input<boolean>(false);

  /**
   * Colección de errores de validación asociados al campo.
   */
  readonly errors = input<readonly WithOptionalFieldTree<ValidationError>[]>([]);

  /* Gestión de la visibilidad */

  /**
   * Estado interno que indica si la contraseña es visible o está oculta.
   */
  protected readonly visibility = signal(false);

  /**
   * Estado computado que define el tipo del input, el icono de visibilidad y el texto del título según el estado actual.
   */
  protected readonly state = computed(() =>
    this.visibility()
      ? { type: 'text', icon: 'visibility_off', title: $localize`Hide` }
      : { type: 'password', icon: 'visibility', title: $localize`Show` },
  );

  /**
   * Maneja el evento de clic en el botón para alternar la visibilidad de la contraseña.
   *
   * @param e Evento de ratón generado al hacer clic en el botón.
   */
  protected onIconClick(e: MouseEvent) {
    e.stopPropagation();
    this.visibility.update((state) => !state);
  }
}
