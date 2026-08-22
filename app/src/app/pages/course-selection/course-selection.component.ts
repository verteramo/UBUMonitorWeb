import { Component, computed, effect, inject, signal } from '@angular/core';
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
import { Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { COURSE_CLASSIFICATION, CourseService } from '@core/api/course.service';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
import { ThemeToggleComponent } from '@shared/components/theme-toggle/theme-toggle.component';

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
  ],
  templateUrl: './course-selection.component.html',
  styleUrl: './course-selection.component.scss',
})
export class CourseSelectionComponent {
  private router = inject(Router);
  private authService = inject(AuthService);
  private courseService = inject(CourseService);
  private sessionStore = inject(SessionStore);

  get principal() {
    return this.sessionStore.principal() as Principal;
  }

  $term = signal('');
  $tab = signal(0);
  $sort = signal<Sort>({ active: '', direction: '' });
  $course = signal<Course | null>(null);

  $courses = rxResource({
    defaultValue: [],
    params: () => COURSE_CLASSIFICATION[this.$tab()],
    stream: ({ params: classification }) => this.courseService.getCourses(classification),
  });

  columns = ['select', 'name', 'category'];

  $filteredCourses = computed(() => {
    const term = this.$term().trim().toLowerCase();
    const courses = this.$courses.value();

    let filtered = !term
      ? [...courses]
      : courses.filter((course) => course.fullname.toLowerCase().includes(term));

    const { active, direction } = this.$sort();

    if (active && direction) {
      filtered.sort((a, b) => {
        const course_a = active === 'name' ? a.fullname : a.category.name;
        const course_b = active === 'name' ? b.fullname : b.category.name;
        return course_a.localeCompare(course_b) * (direction === 'asc' ? 1 : -1);
      });
    }

    return filtered;
  });

  constructor() {
    effect(() => {
      const course = this.$course();

      if (course) {
        this.sessionStore.setCourse(course);
      }
    });
  }

  onSelect(): void {
    this.router.navigate(['/dashboard']);
  }

  logout(): void {
    this.authService.logout();
  }
}
