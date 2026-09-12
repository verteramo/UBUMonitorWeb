import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTreeModule } from '@angular/material/tree';
import { Module } from '@core/models/module';
import { Section } from '@core/models/section';
import { withDatasetSlice } from '@core/stores/features/dataset-slice.feature';
import { withFilters } from '@core/stores/features/filters.feature';
import { withSelection } from '@core/stores/features/selection.feature';
import { signalStore, withFeature } from '@ngrx/signals';
import { FilterControlComponent } from '@shared/components/filter-control.component';
import ItemComponent from './item.component';

const Store = signalStore(
  withDatasetSlice('sections'),
  withFeature(({ sections }) =>
    withFilters<Section, { term: string }>({
      filters: { term: '' },
      transformFn: ({ term }) => ({ term: term.trim().toLowerCase() }),
      items: sections,
      filterFn: ({ term }, section) => section.name?.toLowerCase().includes(term) ?? false,
    }),
  ),
  withFeature(({ filteredItems }) => withSelection(filteredItems, (section) => section.id)),
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
      <mat-tree #tree [dataSource]="store.filteredItems()" [childrenAccessor]="childrenAccessor">
        <!-- Template para Nodos Hoja (Módulos) -->
        <mat-tree-node *matTreeNodeDef="let node" matTreeNodePadding>
          <!-- Botón deshabilitado para mantener la alineación con los padres -->
          <button matIconButton disabled></button>

          <!-- Tu componente visual de módulo -->
          <app-item
            class="tree-item-content"
            [item]="node"
            [selected]="store.isSelected(node.id)"
            (toggle)="store.toggleItem($event)"
          />
        </mat-tree-node>

        <!-- Template para Nodos Expandibles (Secciones) -->
        <mat-tree-node
          *matTreeNodeDef="let node; when: hasChild"
          matTreeNodePadding
          matTreeNodeToggle
        >
          <!-- Botón de expandir/colapsar -->
          <button matIconButton matTreeNodeToggle [attr.aria-label]="'Toggle ' + node.name">
            <mat-icon class="mat-icon-rtl-mirror">
              {{ tree.isExpanded(node) ? 'expand_more' : 'chevron_right' }}
            </mat-icon>
          </button>

          <!-- Tu componente visual de sección -->
          <app-item
            class="tree-item-content"
            [item]="node"
            [selected]="store.isSelected(node.id)"
            (toggle)="store.toggleItem($event)"
          />
        </mat-tree-node>
      </mat-tree>
    </main>
  `,
})
export class TabLogsSectionsComponent {
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
