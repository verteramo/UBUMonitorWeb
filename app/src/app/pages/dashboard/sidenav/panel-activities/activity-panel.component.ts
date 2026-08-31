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
import { FilterControlComponent } from '../components/filter-control.component';
import SectionArticleComponent from './section-article.component';

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
    SectionArticleComponent,
    FilterControlComponent,
  ],
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      flex: 1;
      height: 100%;
      min-height: 0;
      overflow: hidden;
    }

    main {
      flex: 1;
      min-height: 0;
      overflow-y: auto;
    }
  `,
  template: `
    @if (store.isLoading()) {
      <app-progress-spinner i18n>Loading sections...</app-progress-spinner>
    } @else {
      <header>
        <app-filter-control i18n-placeholder placeholder="Filter..."> </app-filter-control>
      </header>

      <main>
        @for (section of store.filteredSections(); track section.id) {
          <app-section-article [section]="section" />
        }
      </main>
    }
  `,
})
export class ActivityPanelComponent {
  readonly store = inject(ActivitiesStore);
}
