import { signalStoreFeature, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { debounce, interval, Observable, pipe, Subject, switchMap } from 'rxjs';

export function withAutoSave<T>(
  saveFnFactory: () => (data: T) => Observable<unknown>,
  delayFn: () => number = () => 1000,
) {
  return signalStoreFeature(
    withMethods(() => {
      const saveFn = saveFnFactory();
      const trigger$ = new Subject<T>();

      const effect = rxMethod<T>(
        pipe(
          debounce(() => interval(delayFn())), // Se recalcula dinámicamente en cada emisión
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
