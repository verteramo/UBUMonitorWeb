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
    components: computed(() => [
      ...new Set(
        logs()
          .flatMap(({ component }) => component)
          .filter(Boolean),
      ),
    ]),
  })),
  withFeature(({ components }) =>
    withFilters<string, { term: string }>({
      filters: { term: '' },
      transformFn: ({ term }) => ({ term: term.trim().toLowerCase() }),
      items: components,
      filterFn: ({ term }, component) => component.toLowerCase().includes(term),
    }),
  ),
  withFeature(({ filteredItems }) => withSelection(filteredItems, (component) => component)),
  withSettingsSlice('components'),
);

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
