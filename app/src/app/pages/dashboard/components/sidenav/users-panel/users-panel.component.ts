/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { User } from '@core/models/user';
import { UsersStore } from '@core/stores/users.store';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import { UserArticleComponent } from './components/user-article/user-article.component';
import { UserDialogComponent } from './components/user-dialog/user-dialog.component';

/** Componente del panel de usuarios. */
@Component({
  selector: 'app-users-panel',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatTabsModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatCheckboxModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    UserArticleComponent,
    ProgressSpinnerComponent,
    MatBadgeModule,
  ],
  templateUrl: 'users-panel.component.html',
  styleUrls: ['users-panel.component.scss'],
})
export class UsersPanelComponent {
  readonly dialog = inject(MatDialog);
  readonly store = inject(UsersStore);

  /** Determina si están todos los usuarios seleccionados. */
  readonly isAllSelected = computed(() => {
    const users = this.store.filteredUsers();
    const selected = this.store.selectionSet();

    return users.length && users.every(({ id }) => selected.has(id));
  });

  /** Determina si se trata de una selección parcial. */
  readonly isPartiallySelected = computed(() => {
    const users = this.store.filteredUsers();
    const selected = this.store.selectionSet();

    return !this.isAllSelected() && users.some(({ id }) => selected.has(id));
  });

  /** Abre el diálogo con el perfil de un usuario. */
  openProfile(user: User) {
    this.dialog.open(UserDialogComponent, { data: user });
  }

  /** Cambia el estado de selección de los usuarios filtrados (visibles actualmente). */
  toggleAll() {
    const filtered = this.store.filteredUsers().map(({ id }) => id);
    this.store.toggleItems(filtered);
  }
}
