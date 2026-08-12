import { Component, computed, inject, OnInit, signal } from '@angular/core';
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
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { CourseService } from '@core/api/course.service';
import { Course } from '@core/models/course';
import { ProblemDetail } from '@core/models/problem-detail';
import { SnackService } from '@core/services/snack.service';
import { CourseStore } from '@core/store/course.store';
import { PrincipalStore } from '@core/store/principal.store';

export interface SyncOptions {
  updateData: boolean;
  logs: boolean;
  grades: boolean;
  completeAct: boolean;
}

const TAB_ENDPOINTS = ['all', 'starred', 'recent', 'inprogress', 'future', 'past'];

function compare(a: string, b: string, isAsc: boolean) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}

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
  ],
  templateUrl: './course-selection.component.html',
  styleUrl: './course-selection.component.scss',
})
export class CourseSelectionComponent implements OnInit {
  private authService = inject(AuthService);
  private courseService = inject(CourseService);
  private snackService = inject(SnackService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private principalStore = inject(PrincipalStore);
  private courseStore = inject(CourseStore);

  $title = signal(this.route.snapshot.title || 'Selección de curso/asignatura');
  $principal = this.principalStore.$value;

  $isLoading = signal(true);
  $activeTab = signal(0);
  $searchTerm = signal('');
  $courseCache = signal<Record<string, Course[]>>({});
  $selectedCourse = signal<Course | null>(null);
  $sortState = signal<Sort>({ active: '', direction: '' });

  $updateData = signal(false);
  $logs = signal(false);
  $grades = signal(false);
  $completeAct = signal(false);

  displayedColumns: string[] = ['select', 'favorite', 'name', 'category'];

  $filteredCourses = computed<Course[]>(() => {
    const category = TAB_ENDPOINTS[this.$activeTab()];
    const courses = this.$courseCache()[category] || [];
    const term = this.$searchTerm().toLowerCase();

    let filtered = term
      ? courses.filter((c) => c.fullname.toLowerCase().includes(term))
      : [...courses];
    const { active, direction } = this.$sortState();

    if (active && direction) {
      filtered.sort((a, b) => {
        const isAsc = direction === 'asc';
        switch (active) {
          case 'name':
            return compare(a.fullname, b.fullname, isAsc);
          case 'category':
            return compare(a.category?.name || '', b.category?.name || '', isAsc);
          default:
            return 0;
        }
      });
    }

    return filtered;
  });

  ngOnInit(): void {
    this.loadCategory(0);
  }

  onTabChange(index: number): void {
    this.$activeTab.set(index);
    this.loadCategory(index);
  }

  onSortChange(sort: Sort): void {
    this.$sortState.set(sort);
  }

  private loadCategory(index: number): void {
    const category = TAB_ENDPOINTS[index];
    if (this.$courseCache()[category]) return;

    this.$isLoading.set(true);
    this.courseService.getCourses(category).subscribe({
      next: (courses) => {
        this.$courseCache.update((cache) => ({ ...cache, [category]: courses }));
        this.$isLoading.set(false);
      },
      error: ({ detail }: ProblemDetail) => {
        this.snackService.show(detail);
        this.$isLoading.set(false);
      },
    });
  }

  onSelectCourse(): void {
    const course = this.$selectedCourse();
    if (course) {
      this.courseStore.set(course);

      const payload = {
        course,
        options: {
          updateData: this.$updateData(),
          logs: this.$logs(),
          grades: this.$grades(),
          completeAct: this.$completeAct(),
        } as SyncOptions,
      };
      console.log('Sincronizando:', payload);

      this.router.navigate(['/dashboard']);
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
