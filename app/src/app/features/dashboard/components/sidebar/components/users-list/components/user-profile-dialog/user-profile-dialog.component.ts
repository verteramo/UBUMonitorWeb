import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-user-profile-dialog',
  standalone: true,
  imports: [MatDialogModule, DatePipe],
  template: `
    <div style="padding: 24px; min-width: 750px; font-family: sans-serif;">
      <h2 style="text-align: center; font-size: 28px; font-weight: bold; margin-bottom: 32px;">
        {{ user.fullname }}
      </h2>

      <div style="display: flex; gap: 40px;">
        <div style="flex: 1; display: flex; flex-direction: column; gap: 24px;">
          <img [src]="user.profileimageurl" alt="Avatar" style="width: 120px; height: 120px;" />
          <a [href]="'mailto:' + user.email" style="color: #0088cc; text-decoration: none;">
            {{ user.email }}
          </a>

          <div
            style="display: grid; grid-template-columns: 180px 1fr; align-items: center; gap: 16px;"
          >
            <span>Último acceso Curso</span>
            <div style="display: flex; align-items: center; gap: 8px;">
              <div
                style="width: 20px; height: 20px; border-radius: 50%; background-color: #ff0000;"
              ></div>
              <span>{{
                user.lastcourseaccess ? (user.lastcourseaccess | date: 'dd/MM/yy HH:mm') : 'Nunca'
              }}</span>
            </div>

            <span>Último acceso Moodle</span>
            <div style="display: flex; align-items: center; gap: 8px;">
              <div
                style="width: 20px; height: 20px; border-radius: 50%; background-color: #ff0000;"
              ></div>
              <span>{{
                user.lastaccess ? (user.lastaccess | date: 'dd/MM/yy HH:mm') : 'Nunca'
              }}</span>
            </div>

            <span>Primer acceso Moodle</span>
            <span>{{
              user.firstaccess ? (user.firstaccess | date: 'dd/MM/yy HH:mm') : 'Nunca'
            }}</span>

            <span>Roles</span>
            <span>{{ getNames(user.roles) }}</span>

            <span>Grupos</span>
            <span>{{ getNames(user.groups) }}</span>

            <span>Nº Cursos matriculados</span>
            <span>{{ user.enrolledcourses?.length || 0 }}</span>
          </div>
        </div>

        <div style="flex: 1;">
          <div style="border: 1px solid #7eb4d4; border-radius: 2px;">
            <div
              style="background-color: #e0e0e0; background-image: linear-gradient(to bottom, #f9f9f9, #d0d0d0); padding: 8px; text-align: center; font-weight: bold; border-bottom: 1px solid #999;"
            >
              Cursos matriculados
            </div>
            <div style="max-height: 400px; overflow-y: auto; background: white;">
              @for (course of user.enrolledcourses; track course.id; let even = $even) {
                <div
                  [style.background-color]="even ? '#00a2d9' : '#ffffff'"
                  [style.color]="even ? 'white' : 'black'"
                  style="padding: 6px 12px; border-bottom: 1px solid #eee;"
                >
                  {{ course.fullname }}
                </div>
              } @empty {
                <div style="padding: 12px; text-align: center; color: #666;">Sin cursos</div>
              }
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class UserProfileDialogComponent {
  user = inject(MAT_DIALOG_DATA);

  getNames(items: any[] | undefined): string {
    return items?.map((item) => item.name).join(', ') || '';
  }
}
