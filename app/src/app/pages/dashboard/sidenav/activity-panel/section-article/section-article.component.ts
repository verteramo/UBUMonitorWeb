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
  templateUrl: './section-article.component.html',
  styleUrl: './section-article.component.scss',
})
export default class SectionArticleComponent {
  section = input.required<Section>();
  selected = input<boolean>();
  toggle = output<number>();
}
