/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { SchemaPath, validate, ValidationError } from '@angular/forms/signals';

/**
 * Validador del URLs.
 *
 * @see https://angular.dev/guide/forms/signals/validation#reusable-validation-rules
 *
 * @param path Campo del formulario a validar
 * @param config Configuración de la validación (por ejemplo el mensaje `message`)
 */
export function url(path: SchemaPath<string>, config?: Partial<ValidationError>) {
  validate(path, ({ value }) => {
    try {
      new URL(value());
      return null;
    } catch {
      return {
        kind: 'url',
        message: $localize`Invalid URL`,
        ...config,
      };
    }
  });
}
