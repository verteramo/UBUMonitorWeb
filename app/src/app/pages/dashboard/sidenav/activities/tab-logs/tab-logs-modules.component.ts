import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTreeModule } from '@angular/material/tree';
import { withCollection } from '@core/stores/features/collection.feature';
import { signalStore } from '@ngrx/signals';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import ItemComponent from '../components/item.component';

const Store = signalStore(
  withCollection({
    selectionKey: 'modules',
    selector: (dataset) => dataset.modules,
    identifier: (module) => module.id,
    searchBy: (module) => module.name,
  }),
);

@Component({
  selector: 'app-tab-logs-modules',
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
      @for (module of store.filteredItems(); track $index) {
        <app-item
          [item]="module"
          [selected]="store.isSelected(module)"
          (toggle)="store.toggleItem(module)"
        />
      }
    </main>
  `,
})
export class TabLogsModulesComponent {
  protected readonly store = inject(Store);
}
