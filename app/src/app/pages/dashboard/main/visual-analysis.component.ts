import { Component, computed, inject } from '@angular/core';
import { DatasetStore } from '@core/stores/dataset.store';
import { SelectionStore } from '@core/stores/selection.store';
import { BarChartComponent, BarChartItem } from '../charts/bar-chart.component';

@Component({
  selector: 'app-visual-analysis',
  standalone: true,
  imports: [BarChartComponent],
  template: `
    <app-bar-chart
      [data]="chartData()"
      title="Registros por componente"
      xAxisName="Componentes"
      yAxisName="Registros"
      emptyMessage="Selecciona al menos un componente y un usuario en los paneles laterales."
    />
  `,
})
export class VisualAnalysisComponent {
  private dataset = inject(DatasetStore);
  private selection = inject(SelectionStore);

  chartData = computed<BarChartItem[]>(() => {
    const selectedComponents = this.selection.components();
    const selectedUsers = this.selection.participants();

    const logs = this.dataset.data.value().logs;

    if (selectedComponents.length === 0 || selectedUsers.length === 0 || logs.length === 0) {
      return [];
    }

    const activeUsersSet = new Set(selectedUsers);
    const logsCountByComponent = new Map<string, number>();

    selectedComponents.forEach((comp) => logsCountByComponent.set(comp, 0));

    for (const log of logs) {
      if (logsCountByComponent.has(log.component)) {
        const userId = log.attributes?.userId;

        if (userId !== undefined && activeUsersSet.has(userId)) {
          const currentCount = logsCountByComponent.get(log.component)!;
          logsCountByComponent.set(log.component, currentCount + 1);
        }
      }
    }

    return Array.from(logsCountByComponent.entries()).map(([label, value]) => ({
      label,
      value,
    }));
  });
}
