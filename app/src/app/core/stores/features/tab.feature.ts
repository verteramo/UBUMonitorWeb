import { computed } from '@angular/core';
import {
  patchState,
  signalStoreFeature,
  type,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';

/**
 * Feature que añade la funcionalidad para administrar pestañas.
 *
 * @template T Tipo de los identificadores de las pestañas.
 * @param tabs Array de pestañas disponibles.
 * @returns Feature del store con el estado de la pestaña activa, el índice computado y el método para cambiarla.
 */
export function withTabs<T extends string>(...tabs: readonly T[]) {
  return signalStoreFeature(
    withState<{ tab: T }>({ tab: tabs[0] }),
    withComputed(({ tab }) => ({
      /**
       * Índice numérico correspondiente a la pestaña activa actual.
       */
      tabIndex: computed(() => tabs.indexOf(tab())),
    })),
    withMethods((store) => ({
      /**
       * Establece la pestaña activa en función de su índice numérico.
       *
       * @param index Índice de la pestaña que se desea seleccionar.
       */
      setTabIndex(index: number): void {
        patchState(store, { tab: tabs[index] });
      },
    })),
  );
}

export function withPanels<
  const Config extends Record<string, readonly string[]>,
  Panel extends keyof Config & string,
>(config: Config) {
  /**
   * Paneles extraídos de las keys de la configuración.
   */
  const panels = Object.keys(config) as Panel[];

  return signalStoreFeature(
    /*
     * Requiere la existencia previa de la propiedad 'tab',
     * lo más habitual sería debido a la utilización de la feature 'withTabs'.
     */
    { state: type<{ tab: string }>() },

    withState({
      /**
       * Configuración del estado de expansión de los paneles,
       * en primera instancia solo está expandido el primero.
       */
      panelsExpanded: panels.reduce(
        (acc, panel, index) => ({ ...acc, [panel]: index === 0 }),
        {},
      ) as Record<Panel, boolean>,
    }),

    withComputed(({ tab }) => ({
      /**
       * Configuración de visibilidad de los paneles de acuerdo con la pestaña activa.
       */
      panelsVisibility: computed(
        () =>
          panels.reduce(
            (acc, panel) => ({ ...acc, [panel]: config[panel].includes(tab()) }),
            {},
          ) as Record<Panel, boolean>,
      ),
    })),

    withMethods((store) => ({
      /**
       * Determina si un panel es visible.
       *
       * @param panel Panel.
       */
      isPanelVisible(panel: Panel): boolean {
        return store.panelsVisibility()[panel];
      },

      /**
       * Determina si un panel está expandido.
       *
       * @param panel Panel.
       */
      isPanelExpanded(panel: Panel): boolean {
        return store.panelsExpanded()[panel];
      },

      /**
       * Establece el estado de expansión de un panel.
       *
       * @param panel Panel.
       * @param expanded Nuevo estado de expansión.
       */
      setPanelExpanded(panel: Panel, expanded: boolean): void {
        patchState(store, ({ panelsExpanded }) => ({
          panelsExpanded: {
            ...panelsExpanded,
            [panel]: expanded,
          },
        }));
      },

      /**
       * Intercambia el estado de expansión de un panel.
       *
       * @param panel Panel.
       */
      togglePanel(panel: Panel): void {
        patchState(store, ({ panelsExpanded }) => ({
          panelsExpanded: {
            ...panelsExpanded,
            [panel]: !panelsExpanded[panel],
          },
        }));
      },
    })),
  );
}
