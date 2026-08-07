import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '@core/api/auth.service';
import { ProblemDetail } from '@core/models/problem-detail';
import { SnackService } from '@core/services/snack.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatListModule,
    MatDividerModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  private router = inject(Router);
  private authService = inject(AuthService);
  private snackService = inject(SnackService);

  $principal = this.authService.getPrincipal();

  logout() {
    this.authService.logout().subscribe({
      error: ({ detail }: ProblemDetail) => {
        this.snackService.show(detail || $localize`Unknown error`);
      },
      complete: () => {
        this.router.navigate(['/login']);
      },
    });
  }
}
