import { Component, computed, input } from '@angular/core';
import type { EChartsOption } from 'echarts';
import { NgxEchartsDirective } from 'ngx-echarts';

export interface BarChartItem {
  label: string;
  value: number;
}

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [NgxEchartsDirective],
  template: `
    @if (data().length > 0) {
      <div echarts [options]="chartOptions()" class="chart-container"></div>
    } @else {
      <div class="empty-state">{{ emptyMessage() }}</div>
    }
  `,
  styles: [
    `
      .chart-container {
        height: 450px;
        width: 100%;
      }
      .empty-state {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 450px;
        color: #666;
        font-style: italic;
      }
    `,
  ],
})
export class BarChartComponent {
  // Datos requeridos
  data = input.required<BarChartItem[]>();

  // Configuraciones opcionales con valores por defecto
  title = input<string>('');
  xAxisName = input<string>('');
  yAxisName = input<string>('');
  color = input<string>('#3f51b5');
  emptyMessage = input<string>('No hay datos para mostrar');

  chartOptions = computed<EChartsOption>(() => {
    const chartData = this.data();

    return {
      title: {
        text: this.title(),
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        name: this.xAxisName(),
        data: chartData.map((d) => d.label),
        axisLabel: { rotate: 45, interval: 0 },
      },
      yAxis: {
        type: 'value',
        name: this.yAxisName(),
        minInterval: 1,
      },
      series: [
        {
          type: 'bar',
          barWidth: '50%',
          data: chartData.map((d) => d.value),
          itemStyle: {
            color: this.color(),
            borderRadius: [4, 4, 0, 0],
          },
        },
      ],
    };
  });
}
