import { Component, computed, input, model, signal } from '@angular/core';
import { FormValueControl } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-password-field',
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
        autocomplete="current-password"
      />
      <button matSuffix matIconButton type="button" (click)="visibility.update((state) => !state)">
        <mat-icon [title]="state().title">{{ state().icon }}</mat-icon>
      </button>
    </mat-form-field>
  `,
})
export class PasswordFieldComponent implements FormValueControl<string> {
  readonly value = model('');
  readonly label = input.required<string>();

  protected readonly visibility = signal(false);

  protected readonly state = computed(() =>
    this.visibility()
      ? { type: 'text', icon: 'visibility_off', title: $localize`Hide` }
      : { type: 'password', icon: 'visibility', title: $localize`Show` },
  );
}
