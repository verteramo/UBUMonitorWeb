import { patchState, signalStoreFeature, withMethods, withState } from '@ngrx/signals';

type LoadingState = {
  loading: boolean;
};

export function extractLoadingFromState<T extends Record<string, any>>(state: T & LoadingState): T {
  const { loading, ...props } = state;
  return props as unknown as T;
}

export function withLoading() {
  return signalStoreFeature(
    withState<LoadingState>({ loading: false }),
    withMethods((store) => ({
      startLoading(): void {
        patchState(store, { loading: true });
      },

      stopLoading(): void {
        patchState(store, { loading: false });
      },
    })),
  );
}
