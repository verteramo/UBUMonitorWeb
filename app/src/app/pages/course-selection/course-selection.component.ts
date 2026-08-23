import { Component, computed, inject, signal } from '@angular/core';
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
import { CourseClassifications, CourseService } from '@core/services/course.service';
import { SessionStore } from '@core/stores/session.store';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { ThemeToggleComponent } from '@shared/components/theme-toggle.component';

/**
 * Componente de selección de cursos.
 *
 * @author Marcelo Verteramo Pérsico
 */
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
  private courseService = inject(CourseService);
  private sessionStore = inject(SessionStore);

  principal = this.sessionStore.currentPrincipal;

  term = signal('');
  tab = signal(0);
  sort = signal<Sort>({ active: '', direction: '' });
  course = signal<Course | null>(null);

  courses = rxResource({
    defaultValue: [],
    params: () => CourseClassifications[this.tab()],
    stream: ({ params: classification }) => this.courseService.getCourses(classification),
  });

  columns = ['select', 'name', 'category'];

  filteredCourses = computed(() => {
    const term = this.term().trim().toLowerCase();
    const courses = this.courses.value();

    let filtered = !term
      ? [...courses]
      : courses.filter((course) => course.name.toLowerCase().includes(term));

    const { active, direction } = this.sort();

    if (active && direction) {
      filtered.sort((a, b) => {
        const course_a = active === 'name' ? a.name : a.category;
        const course_b = active === 'name' ? b.name : b.category;
        return course_a.localeCompare(course_b) * (direction === 'asc' ? 1 : -1);
      });
    }

    return filtered;
  });

  onSelect(): void {
    this.sessionStore.setCourse(this.course() as Course);
  }

  onLogout(): void {
    this.sessionStore.logout();
  }
}
