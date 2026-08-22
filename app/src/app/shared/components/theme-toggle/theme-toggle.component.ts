import { Component, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SettingsStore, ThemeMode } from '@core/stores/settings.store';

@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [MatButtonModule, MatIconModule],
  template: `
    <button mat-icon-button (click)="cycleTheme()" [title]="nextThemeTooltip()">
      <mat-icon>{{ currentIcon() }}</mat-icon>
    </button>
  `,
})
export class ThemeToggleComponent {
  private settingsStore = inject(SettingsStore);

  private themes: ThemeMode[] = ['system', 'light', 'dark'];
  private icons = { system: 'brightness_auto', light: 'light_mode', dark: 'dark_mode' };

  currentIcon = computed(() => this.icons[this.settingsStore.theme()]);

  nextThemeTooltip = computed(() => {
    const current = this.settingsStore.theme();
    const nextTheme = this.themes[(this.themes.indexOf(current) + 1) % this.themes.length];
    return $localize`Switch to ${nextTheme} mode`;
  });

  cycleTheme() {
    const current = this.settingsStore.theme();
    const nextTheme = this.themes[(this.themes.indexOf(current) + 1) % this.themes.length];
    this.settingsStore.setTheme(nextTheme);
  }
}
