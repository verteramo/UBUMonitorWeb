// import { Component, input } from '@angular/core';
// import { FilterControlComponent } from './filter-control.component';

// type FilterControls = {
//   isCompleteSelection(): boolean;
//   isPartialSelection(): boolean;
//   activeFiltersCount(): number;
//   term(): string;
//   setTerm(term: string): void;
//   toggleItems(): void;
// };

// type Item = {
//   text: string;
//   isSelected(): boolean;
//   toggle(status: boolean): void;
// };

// @Component({
//   selector: 'app-filterable-list',
//   standalone: true,
//   imports: [FilterControlComponent],
//   styles: ``,
//   template: `
//     <header>
//       @let controls = filterControls();
//       <app-filter-control
//         i18n-placeholder
//         placeholder="Filter..."
//         [checked]="controls.isCompleteSelection()"
//         [indeterminate]="controls.isPartialSelection()"
//         [badge]="controls.activeFiltersCount()"
//         [term]="controls.term()"
//         (termChange)="controls.setTerm($event)"
//         (toggleAll)="controls.toggleItems()"
//       />
//     </header>

//         <main>
//       @for (item of items(); track $index) {
//         <app-item
//           class="tree-item-content"
//           [item]="item.text"
//           [selected]="item.isSelected()"
//           (toggle)="item.toggle($event)"
//         />
//       }
//     </main>
//   `,
// })
// export class FilterableListComponent {
//   readonly filterControls = input.required<FilterControls>();
//   readonly items = input.required<Item[]>
// }
