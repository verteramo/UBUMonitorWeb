/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { form, FormField } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { SessionStore } from '@core/stores/session.store';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';
import { CourseSelectionStore } from './course-selection.store';

/** Componente de selección de cursos. */
@Component({
  selector: 'app-course-selection',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatTableModule,
    MatTabsModule,
    MatSortModule,
    MatIconModule,
    ThemeToggleComponent,
    ProgressSpinnerComponent,
    FormField
],
  providers: [CourseSelectionStore],
  templateUrl: './course-selection.component.html',
  styleUrl: './course-selection.component.scss',
})
export class CourseSelectionComponent {
  protected readonly store = inject(CourseSelectionStore);
  protected readonly session = inject(SessionStore);
  protected readonly columns = ['select', 'name', 'category'];

  protected readonly form = form(this.store.model);

  protected onSubmit(e: Event) {
    const selectedCourse = this.store.selectedCourse();

    if (selectedCourse) {
      this.session.setCourse(selectedCourse);
    }
  }
}
