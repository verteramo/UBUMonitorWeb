import { signalStoreFeature, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { debounceTime, Observable, pipe, Subject, switchMap } from 'rxjs';

export function withAutoSave<T>(
  saveFnFactory: () => (data: T) => Observable<unknown>,
  delay = 1000,
) {
  return signalStoreFeature(
    withMethods(() => {
      // Se ejecuta inmediatamente en el contexto de inyección del store
      const saveFn = saveFnFactory();
      const trigger$ = new Subject<T>();

      const effect = rxMethod<T>(
        pipe(
          debounceTime(delay),
          switchMap((data) => saveFn(data)),
        ),
      );

      effect(trigger$);

      return {
        save: (data: T) => trigger$.next(data),
      };
    }),
  );
}
