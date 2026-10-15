import { Component, input } from '@angular/core';

export type AvatarData = {
  picture?: string | null;
  fullName: string;
};

@Component({
  selector: 'app-avatar',
  standalone: true,
  host: {
    '[style.width.px]': 'size()',
    '[style.height.px]': 'size()',
  },
  styles: `
    :host {
      display: flex;
      box-sizing: border-box;
      border-radius: 50%;
      border: 1px solid var(--mat-sys-outline, #79747e);
      background-color: var(--mat-sys-surface-container, #fff);
      overflow: hidden;
      flex-shrink: 0;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  `,
  template: ` <img [src]="data().picture || 'user_blank.png'" [alt]="data().fullName" /> `,
})
export class AvatarComponent {
  data = input.required<AvatarData>();
  size = input<number>(32);
}
