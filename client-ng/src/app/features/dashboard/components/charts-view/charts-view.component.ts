import { Component } from '@angular/core';

@Component({
  selector: 'app-charts-view',
  standalone: true,
  template: `
    <div style="height: 100%; display: flex; flex-direction: column;">
      <h2 style="margin-top: 0;">Gráficos y filtros activos</h2>
      <div
        style="flex: 1; border: 2px dashed #ccc; border-radius: 8px; display: grid; place-items: center; color: #888;"
      >
        Zona central de visualización
      </div>
    </div>
  `,
})
export class ChartsViewComponent {}
