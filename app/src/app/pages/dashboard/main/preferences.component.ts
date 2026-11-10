/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { UserPreferencesStore } from '@core/stores/user-preferences.store';

@Component({
  selector: 'app-preferences',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
  ],
  template: `
    <h2 mat-dialog-title i18n>Settings</h2>

    <mat-dialog-content class="settings-content">
      <div class="settings-container">
        <!-- Selección de Tema -->
        <mat-form-field appearance="outline" class="setting-field">
          <mat-label i18n>Theme</mat-label>
          <mat-select
            [value]="preferencesStore.theme()"
            (selectionChange)="preferencesStore.setTheme($event.value)"
          >
            <mat-option value="system" i18n>System</mat-option>
            <mat-option value="light" i18n>Light</mat-option>
            <mat-option value="dark" i18n>Dark</mat-option>
          </mat-select>
        </mat-form-field>

        <!-- Selección de Idioma -->
        <mat-form-field appearance="outline" class="setting-field">
          <mat-label i18n>Language</mat-label>
          <mat-select
            [value]="preferencesStore.language()"
            (selectionChange)="preferencesStore.setLanguage($event.value)"
          >
            <mat-option value="en">English</mat-option>
            <mat-option value="es">Español</mat-option>
          </mat-select>
        </mat-form-field>

        <!-- Retardo de Auto-guardado -->
        <mat-form-field appearance="outline" class="setting-field">
          <mat-label i18n>Autosave Delay (ms)</mat-label>
          <input
            matInput
            type="number"
            [value]="preferencesStore.autosaveDelayMs()"
            (input)="onAutosaveChange($event)"
          />
        </mat-form-field>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close i18n>Close</button>
    </mat-dialog-actions>
  `,
  styles: `
    .settings-content {
      display: flex;
      flex-direction: column;
      height: calc(100% - 120px);
      padding-top: 10px !important;
    }

    .settings-container {
      display: flex;
      flex-direction: column;
      gap: 16px;
      max-width: 300px;
      width: 100%;
    }

    .setting-field {
      width: 100%;
    }
  `,
})
export class PreferencesComponent {
  readonly preferencesStore = inject(UserPreferencesStore);

  onAutosaveChange(event: Event): void {
    const value = Number((event.target as HTMLInputElement).value);
    if (!isNaN(value)) {
      this.preferencesStore.setAutosaveDelayMs(value);
    }
  }
}
