import { Component, input, output } from '@angular/core';
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { User } from '@core/models/user';
import { TimeAgoPipe } from '@core/pipes/time-ago.pipe';

@Component({
  selector: 'app-user-article',
  imports: [MatIconModule, MatCheckboxModule, TimeAgoPipe, MatButtonModule],
  templateUrl: './user-article.component.html',
  styleUrl: './user-article.component.scss',
})
export class UserArticleComponent {
  user = input.required<User>();
  selected = input<boolean>();

  toggle = output<void>();
  openProfile = output<void>();
}
