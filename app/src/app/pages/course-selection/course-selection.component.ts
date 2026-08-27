/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, inject, linkedSignal, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { Course } from '@core/models/course';
import { CourseClassification, CourseService } from '@core/services/course.service';
import { SessionStore } from '@core/stores/session.store';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';

/** Clasificaciones de las pestañas del componente. */
const classifications: CourseClassification[] = [
  'all',
  'starred',
  'recent',
  'inprogress',
  'future',
  'past',
];

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
  ],
  templateUrl: './course-selection.component.html',
  styleUrl: './course-selection.component.scss',
})
export class CourseSelectionComponent {
  #sessionStore = inject(SessionStore);
  #courseService = inject(CourseService);

  /** Columnas de la tabla. */
  columns = ['select', 'name', 'category'];

  /** Cursos clasificados desde el servicio. */
  coursesResource = rxResource({
    defaultValue: [],
    params: () => classifications[this.tab()],
    stream: ({ params: classification }) => this.#courseService.getCourses(classification),
  });

  /** Usuario autenticado. */
  principal = this.#sessionStore.currentPrincipal;

  /** Pestaña seleccionada. */
  tab = signal(1);

  /** Término de búsqueda (se resetea al cambiar de tab). */
  term = linkedSignal({
    source: this.tab,
    computation: () => '',
  });

  /** Estado de ordenación (se resetea al cambiar de tab). */
  sort = linkedSignal<number, Sort>({
    source: this.tab,
    computation: () => ({ active: '', direction: '' }),
  });

  /** Curso seleccionado (se resetea al cambiar de tab). */
  course = linkedSignal<number, Course | null>({
    source: this.tab,
    computation: () => null,
  });

  /** Cursos filtrados y ordenados. */
  courses = computed(() => {
    const courses = this.coursesResource.value();
    const term = this.term().trim().toLowerCase();

    // Filtrado por término de búsqueda
    let filtered = !term
      ? [...courses]
      : courses.filter((course) => course.name.toLowerCase().includes(term));

    // Estado de ordenación
    const { active, direction } = this.sort();

    // Ordenación de los cursos filtrados
    if (active && direction) {
      filtered.sort((a, b) => {
        const course_a = active === 'name' ? a.name : a.category;
        const course_b = active === 'name' ? b.name : b.category;
        return course_a.localeCompare(course_b) * (direction === 'asc' ? 1 : -1);
      });
    }

    return filtered;
  });

  /** Establece el curso seleccionado en el estado de la sesión. */
  onSelect(): void {
    this.#sessionStore.setCourse(this.course() as Course);
  }

  /** Finaliza la sesión. */
  onLogout(): void {
    this.#sessionStore.logout();
  }
}
