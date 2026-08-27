/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { ActivitiesStore } from '@core/stores/activities.store';
import { ProgressSpinnerComponent } from '@shared/components/progress-spinner.component';
import SectionArticleComponent from './section-article/section-article.component';

/** Componente del panel de actividades. */
@Component({
  selector: 'app-activity-panel',
  standalone: true,
  imports: [
    FormsModule,
    ProgressSpinnerComponent,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatSelectModule,
    SectionArticleComponent
  ],
  templateUrl: './activity-panel.component.html',
  styleUrl: './activity-panel.component.scss',
})
export class ActivityPanelComponent {
  readonly store = inject(ActivitiesStore);
}
