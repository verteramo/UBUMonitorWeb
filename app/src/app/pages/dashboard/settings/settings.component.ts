import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AccessCriterion, SettingsStore } from '@core/stores/settings.store';
import { NgxColorsComponent } from 'ngx-colors';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTableModule,
    MatTooltipModule,
    NgxColorsComponent,
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {
  readonly store = inject(SettingsStore);
  private dialogRef = inject(MatDialogRef<SettingsComponent>);

  readonly displayedColumns = ['days', 'color', 'actions'];

  // Estado local dividido para facilitar el bindeo
  readonly criteria = signal<AccessCriterion[]>([]);
  fallbackColor = signal<string>('');

  ngOnInit(): void {
    const currentAccesses = this.store.accesses();

    this.criteria.set(structuredClone(currentAccesses.criteria));
    this.fallbackColor.set(currentAccesses.color);
  }

  addCriterion(): void {
    this.criteria.update(data => [...data, { days: 0, color: '' }]);
  }

  removeCriterion(index: number): void {
    this.criteria.update(data => data.filter((_, i) => i !== index));
  }

  save(): void {
    // Ordenamos de menor a mayor para asegurar coherencia lógica en las fronteras
    const sortedCriteria = [...this.criteria()].sort((a, b) => a.days - b.days);

    this.store.setAccesses({
      color: this.fallbackColor(),
      criteria: sortedCriteria
    });

    this.dialogRef.close();
  }
}
