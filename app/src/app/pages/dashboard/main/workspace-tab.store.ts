import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

export type WorkspaceTab =
  'visual' | 'compare' | 'forums' | 'risk' | 'enrollment' | 'events' | 'clustering';

type WorkspaceTabState = {
  tab: WorkspaceTab;
};

const initialState: WorkspaceTabState = {
  tab: 'visual',
};

export const WorkspaceTabStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store) => ({
    setTab(tab: WorkspaceTab): void {
      patchState(store, { tab });
    },
  })),
);
