import { Component, inject, Signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTreeModule } from '@angular/material/tree';
import { withCollection } from '@core/stores/features/collection.feature';
import { signalStore } from '@ngrx/signals';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import StringItemComponent from '../components/string-item.component';

const Store = signalStore(
  withCollection({
    selectionKey: 'components',
    selector: (dataset) => [...new Set(dataset.logs.map((log) => log.component))],
  }),
);

export interface CollectionStoreContract<Item> {
  term: Signal<string>;
  filteredItems: Signal<Item[]>;
  activeFiltersCount: Signal<number>;
  isCompleteSelection: Signal<boolean>;
  isPartialSelection: Signal<boolean>;
  setTerm: (value: string) => void;
  toggleItems: () => void;
}

@Component({
  selector: 'app-tab-logs-components',
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
      @for (component of store.filteredItems(); track $index) {
        <app-string-item
          class="tree-item-content"
          [item]="component"
          [selected]="store.isSelected(component)"
          (toggle)="store.toggleItem($event)"
        />
      }
    </main>
  `,
})
export class TabLogsComponentsComponent {
  protected readonly store = inject(Store);
}
