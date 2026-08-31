import { Component, input, model } from '@angular/core';
import { FormValueControl } from '@angular/forms/signals';
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
}
