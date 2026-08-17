import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';

interface UserMock {
  id: number;
  fullname: string;
  avatar: string;
  courseDays: string;
  moodleDays: string;
}

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    FormsModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: 'users-list.component.html',
  styleUrls: ['users-list.component.scss'],
})
export class UsersListComponent {
  $searchTerm = signal('amy');
  $selectedRoles = signal<string[]>([]);
  $selectedGroups = signal<string[]>([]);
  $selectedConnections = signal<string[]>([]);

  roles = ['Estudiante', 'Profesor'];
  groups = ['Grupo A', 'Grupo B'];
  connections = ['0-3 días', '3-7 días', '7-14 días', '+14 días'];

  users = signal<UserMock[]>([
    {
      id: 1,
      fullname: 'Anthony Ramirez',
      avatar: 'https://i.pravatar.cc/150?u=1',
      courseDays: '340 días',
      moodleDays: '340 días',
    },
    {
      id: 2,
      fullname: 'Barbara Gardner',
      avatar: 'https://i.pravatar.cc/150?u=2',
      courseDays: '88 días',
      moodleDays: '4 segundos',
    },
    {
      id: 3,
      fullname: 'Brian Franklin',
      avatar: 'https://i.pravatar.cc/150?u=3',
      courseDays: '340 días',
      moodleDays: '340 días',
    },
    {
      id: 4,
      fullname: 'David Ray',
      avatar: 'https://i.pravatar.cc/150?u=4',
      courseDays: '340 días',
      moodleDays: '340 días',
    },
  ]);
}
