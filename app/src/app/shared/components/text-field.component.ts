/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, model, output } from '@angular/core';
import { FormValueControl, ValidationError, WithOptionalFieldTree } from '@angular/forms/signals';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

/**
 * Componente reutilizable de campo de texto con soporte para autocompletado y validación.
 */
@Component({
  selector: 'app-text-field',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatAutocompleteModule,
  ],
  styles: `
    mat-form-field {
      width: 100%;
    }
  `,
  template: `
    <mat-form-field>
      <mat-label>{{ label() }}</mat-label>
      <input
        matInput
        type="text"
        [value]="value()"
        (input)="value.set($event.target.value)"
        (blur)="touch.emit()"
        [autocomplete]="autocomplete()"
        [matAutocomplete]="auto"
      />

      @if (suffix()) {
        <mat-icon matIconSuffix [title]="suffixTitle()">{{ suffixIcon() }}</mat-icon>
      }

      <mat-autocomplete #auto="matAutocomplete" (optionSelected)="value.set($event.option.value)">
        @for (value of list(); track $index) {
          <mat-option [value]="value">{{ value }}</mat-option>
        }
      </mat-autocomplete>
      @if (invalid() && touched()) {
        <mat-error>{{ errors()[0].message }}</mat-error>
      }
    </mat-form-field>
  `,
})
export class InputFieldComponent implements FormValueControl<string> {
  /**
   * Valor actual del campo de texto.
   */
  readonly value = model('');

  /**
   * Etiqueta descriptiva del campo.
   */
  readonly label = input.required<string>();

  /**
   * Configuración de autocompletado del navegador.
   */
  readonly autocomplete = input<string>('');

  /**
   * Lista de opciones disponibles para el autocompletado.
   */
  readonly list = input<string[]>([]);

  /**
   * Indica si se debe mostrar un icono sufijo.
   */
  readonly suffix = input<boolean>(false);

  /**
   * Identificador del icono sufijo de Angular Material.
   */
  readonly suffixIcon = input<string>();

  /**
   * Texto descriptivo (tooltip) del icono sufijo.
   */
  readonly suffixTitle = input<string>();

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
}
