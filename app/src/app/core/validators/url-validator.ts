import { SchemaPath, validate } from '@angular/forms/signals';

/**
 * Validador del URLs.
 *
 * @see https://angular.dev/guide/forms/signals/validation#reusable-validation-rules
 *
 * @param path Campo del formulario a validar
 * @param options Opciones (por ejemplo el mensaje `message`)
 */
export function url(path: SchemaPath<string>, options?: { message?: string }) {
  validate(path, ({ value }) => {
    try {
      new URL(value());
      return null;
    } catch {
      return {
        kind: 'url',
        message: options?.message || $localize`Invalid URL`,
      };
    }
  });
}
