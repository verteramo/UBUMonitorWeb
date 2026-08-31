/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, input, output } from '@angular/core';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';
import { Section } from '@core/models/section';

/** Componente que renderiza los datos de una sección en la lista de actividades. */
@Component({
  selector: 'app-section-article',
  imports: [MatIcon, MatCheckbox],
  template: `
    <article>
      <mat-checkbox
        [checked]="selected()"
        (change)="toggle.emit(section().id)"
        style="pointer-events: none"
      ></mat-checkbox>
      <span>{{ section().name }} </span>

      @if (section().visible) {
        <mat-icon>visibility</mat-icon>
      }

      @if (section().userVisble) {
        <mat-icon>public</mat-icon>
      }
    </article>
  `,
})
export default class SectionArticleComponent {
  section = input.required<Section>();
  selected = input<boolean>();
  toggle = output<number>();
}
