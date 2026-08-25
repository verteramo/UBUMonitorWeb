/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';

/**
 * Estrategia de construcción del título de la aplicación;
 * Consiste en "Nombre componente - Nombre aplicación".
 */
@Injectable()
export class AppTitleStrategy extends TitleStrategy {
  /** Servicio para manipular el título. */
  readonly #title = inject(Title);

  override updateTitle(snapshot: RouterStateSnapshot): void {
    // Título definido en el Router para el componente renderizado
    const pageTitle = this.buildTitle(snapshot);

    if (pageTitle) {
      this.#title.setTitle(`${pageTitle} - UBUMonitorWeb`);
    }
  }
}
