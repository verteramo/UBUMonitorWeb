import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { UserService } from '@core/api/user.service';
import { ProblemDetail } from '@core/models/problem-detail';
import { User } from '@core/models/user';
import { SnackService } from '@core/services/snack.service';
import { CourseStore } from '@core/store/course.store';
import { TimeAgoPipe } from '@shared/pipes/time-ago.pipe';
import { UserProfileDialogComponent } from './components/user-profile-dialog/user-profile-dialog.component';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    TimeAgoPipe,
  ],
  templateUrl: 'users-list.component.html',
  styleUrls: ['users-list.component.scss'],
})
export class UsersListComponent implements OnInit {
  private userService = inject(UserService);
  private courseStore = inject(CourseStore);
  private snackService = inject(SnackService);
  dialog = inject(MatDialog);

  openProfile(user: User): void {
    this.dialog.open(UserProfileDialogComponent, {
      data: user,
      width: '850px',
      maxWidth: '95vw',
    });
  }

  $searchTerm = signal('');
  $selectedEnrollment = signal('enrolled');
  $selectedRoles = signal<string[]>([]);
  $selectedGroups = signal<string[]>([]);
  $selectedConnections = signal<string[]>([]);

  roles = computed(() => {
    const allRoles = this.users()
      .flatMap((user) => user.roles?.map((r) => r.name) ?? [])
      .filter((name) => name && name.trim() !== '');
    return [...new Set(allRoles)];
  });

  groups = computed(() => {
    const allGroups = this.users()
      .flatMap((user) => user.groups?.map((g) => g.name) ?? [])
      .filter((name) => name && name.trim() !== '');
    return [...new Set(allGroups)];
  });

  connections = ['0-3 días', '3-7 días', '7-14 días', '+14 días'];

  users = signal<User[]>([]);

  filteredUsers = computed(() => {
    const term = this.$searchTerm().toLowerCase().trim();
    const selectedRoles = this.$selectedRoles();
    const selectedGroups = this.$selectedGroups();
    const selectedConns = this.$selectedConnections();
    const enrollment = this.$selectedEnrollment();

    const now = new Date().getTime();
    const msPerDay = 1000 * 60 * 60 * 24;

    const filtered = this.users().filter((user) => {
      if (user.roles?.some((r) => r.shortname === 'teacher')) return false;

      // Texto
      if (term && !user.fullname.toLowerCase().includes(term)) return false;

      // Estado de matrícula
      if (enrollment === 'enrolled' && (!user.enrolledcourses || user.enrolledcourses.length === 0))
        return false;
      if (enrollment === 'not_enrolled' && user.enrolledcourses && user.enrolledcourses.length > 0)
        return false;

      // Roles
      if (selectedRoles.length > 0) {
        const userRoles = user.roles?.map((r) => r.name) ?? [];
        if (!userRoles.some((r) => selectedRoles.includes(r))) return false;
      }

      // Grupos
      if (selectedGroups.length > 0) {
        const userGroups = user.groups?.map((g) => g.name) ?? [];
        if (!userGroups.some((g) => selectedGroups.includes(g))) return false;
      }

      // Conexiones
      if (selectedConns.length > 0) {
        let days = -1;
        if (user.lastcourseaccess) {
          days = Math.floor((now - user.lastcourseaccess.getTime()) / msPerDay);
        }

        const matchesTime = selectedConns.some((conn) => {
          if (conn === '0-3 días') return days >= 0 && days <= 3;
          if (conn === '3-7 días') return days > 3 && days <= 7;
          if (conn === '7-14 días') return days > 7 && days <= 14;
          if (conn === '+14 días') return days > 14 || days === -1;
          return false;
        });

        if (!matchesTime) return false;
      }

      return true;
    });

    // Ordenar alfabéticamente por fullname
    return filtered.sort((a, b) => a.fullname.localeCompare(b.fullname));
  });

  constructor() {
    effect(() => {
      const currentCourse = this.courseStore.$value();

      if (currentCourse && currentCourse.id) {
        this.loadUsers(currentCourse.id);
      }
    });
  }

  private loadUsers(courseId: number) {
    this.userService.getUsers(courseId).subscribe({
      next: (users) => this.users.set(users),
      error: ({ detail }: ProblemDetail) => {
        if (detail) {
          this.snackService.show(detail);
        }
      },
    });
  }

  ngOnInit(): void {}
}
