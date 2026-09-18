import { Component, computed, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTreeModule } from '@angular/material/tree';
import { Module } from '@core/models/module';
import { Section } from '@core/models/section';
import { withDatasetSlice } from '@core/stores/features/dataset-slice.feature';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSelection } from '@core/stores/features/selection.feature';
import { withSettingsSlice } from '@core/stores/features/settings-slice.feature';
import { signalStore, withComputed, withFeature } from '@ngrx/signals';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import StringItemComponent from './string-item.component';

const Store = signalStore(
  withDatasetSlice('logs'),
  withComputed(({ logs }) => ({
    events: computed(() => [
      ...new Set(
        logs()
          .flatMap(({ event }) => event)
          .filter(Boolean),
      ),
    ]),
  })),
  withFeature(({ events }) =>
    withFilters<string, { term: string }>({
      filters: { term: '' },
      transformFn: ({ term }) => ({ term: term.trim().toLowerCase() }),
      items: events,
      filterFn: ({ term }, event) => event.toLowerCase().includes(term),
    }),
  ),
  withFeature(({ filteredItems }) => withSelection(filteredItems, (event) => event)),
  withSettingsSlice('events'),
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
  readonly store = inject(Store);

  // Devuelve los hijos de un nodo (en este caso, los módulos de una sección)
  // Si el nodo es un módulo (no tiene propiedad modules), devuelve un array vacío
  childrenAccessor = (node: Section | Module) => {
    return 'modules' in node ? node.modules : [];
  };

  // Determina si un nodo puede expandirse (es una Sección con al menos un módulo)
  hasChild = (_: number, node: Section | Module) => {
    return 'modules' in node && !!node.modules && node.modules.length > 0;
  };
}
