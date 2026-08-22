import { Component } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { UsersListComponent } from './components/users-list/users-list.component';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [MatExpansionModule , UsersListComponent /*, ActivitySelectionComponent */],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {}
