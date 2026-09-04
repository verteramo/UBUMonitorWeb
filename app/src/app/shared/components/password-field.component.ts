import { Component, computed, input, model, output, signal } from '@angular/core';
import { FormValueControl, ValidationError, WithOptionalFieldTree } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

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
  readonly value = model('');
  readonly label = input.required<string>();

  /* Gestión de errores */
  readonly touch = output<void>();
  readonly invalid = input<boolean>(false);
  readonly touched = input<boolean>(false);
  readonly errors = input<readonly WithOptionalFieldTree<ValidationError>[]>([]);

  /* Gestión de la visibilidad */
  protected readonly visibility = signal(false);

  protected readonly state = computed(() =>
    this.visibility()
      ? { type: 'text', icon: 'visibility_off', title: $localize`Hide` }
      : { type: 'password', icon: 'visibility', title: $localize`Show` },
  );

  protected onIconClick(e: MouseEvent) {
    e.stopPropagation();
    this.visibility.update((state) => !state);
  }
}
