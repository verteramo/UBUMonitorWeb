import { SchemaPath, validate } from '@angular/forms/signals';

// https://angular.dev/guide/forms/signals/validation#reusable-validation-rules
export function url(path: SchemaPath<string>, options?: { message?: string }) {
  validate(path, ({ value }) => {
    try {
      new URL(value());
      return null;
    } catch {
      return {
        kind: 'url',
        message: options?.message || 'Enter a valid URL',
      };
    }
  });
}
