import { Component, input, model, output } from '@angular/core';
import { FormValueControl, ValidationError, WithOptionalFieldTree } from '@angular/forms/signals';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

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

      <mat-autocomplete #auto="matAutocomplete">
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
  readonly value = model('');
  readonly label = input.required<string>();
  readonly autocomplete = input<string>('');
  readonly list = input<string[]>([]);
  readonly suffix = input<boolean>(false);
  readonly suffixIcon = input<string>();
  readonly suffixTitle = input<string>();

    /* Gestión de errores */
  readonly touch = output<void>();
  readonly invalid = input<boolean>(false);
  readonly touched = input<boolean>(false);
  readonly errors = input<readonly WithOptionalFieldTree<ValidationError>[]>([]);
}
