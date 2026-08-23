import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

type DashboardState = {
  isSidenavOpened: boolean;
  panels: {
    [panel: string]: boolean;
  };
};

const initialState: DashboardState = {
  isSidenavOpened: true,
  panels: {
    users: true,
  },
};

export const DashboardStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withStorage(localStorage, 'dashboard-store'),
  withMethods((store) => ({
    toggleSidenav() {
      patchState(store, { isSidenavOpened: !store.isSidenavOpened() });
    },

    setSidenavOpened(opened: boolean) {
      patchState(store, { isSidenavOpened: opened });
    },

    setPanelExpanded(panel: string, expanded: boolean) {
      patchState(store, (state) => ({
        ...state.panels,
        panels: {
          [panel]: expanded,
        },
      }));
    },

    isPanelExpanded(panel: string) {
      return store.panels()[panel] || false;
    },
  })),
);
