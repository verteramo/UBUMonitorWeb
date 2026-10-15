import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTreeModule } from '@angular/material/tree';
import { withCollection } from '@core/stores/features/collection.feature';
import { signalStore } from '@ngrx/signals';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import StringItemComponent from '../components/string-item.component';

const Store = signalStore(
  withCollection({
    selectionKey: 'events',
    selector: (dataset) => dataset.events,
  }),
);

@Component({
  selector: 'app-tab-logs-events',
  providers: [Store],
  imports: [FilterControlComponent, MatTreeModule, MatIconModule, StringItemComponent],
  styles: ``,
  template: `
    <header>
      <app-filter-control
        i18n-placeholder
        placeholder="Filter..."
        [checked]="store.isCompleteSelection()"
        [indeterminate]="store.isPartialSelection()"
        [badge]="store.activeFiltersCount()"
        [term]="store.term()"
        (termChange)="store.setTerm($event)"
        (toggleAll)="store.toggleItems()"
      />
    </header>

    <main>
      @for (event of store.filteredItems(); track $index) {
        <app-string-item
          class="tree-item-content"
          [item]="event"
          [selected]="store.isSelected(event)"
          (toggle)="store.toggleItem($event)"
        />
      }
    </main>
  `,
})
export class TabLogsEventsComponent {
  protected readonly store = inject(Store);
}
