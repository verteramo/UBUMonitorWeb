import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTreeModule } from '@angular/material/tree';
import { withCollection } from '@core/stores/features/collection.feature';
import { signalStore } from '@ngrx/signals';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import ItemComponent from '../components/item.component';

const Store = signalStore(
  withCollection({
    selectionKey: 'sections',
    selector: (dataset) => dataset.sections,
    identifier: (section) => section.id,
    searchBy: (section) => section.name,
  }),
);

@Component({
  selector: 'app-tab-logs-sections',
  providers: [Store],
  imports: [FilterControlComponent, MatTreeModule, MatIconModule, ItemComponent],
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
      @for (section of store.filteredItems(); track $index) {
        <app-item
          class="tree-item-content"
          [item]="section"
          [selected]="store.isSelected(section)"
          (toggle)="store.toggleItem(section)"
        />
      }
    </main>
  `,
})
export class TabLogsSectionsComponent {
  protected readonly store = inject(Store);
}
