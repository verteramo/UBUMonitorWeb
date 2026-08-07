import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { ENDPOINTS } from '@core/api/endpoints';
import { Course } from '@core/models/course';
import { Observable } from 'rxjs';

@Service()
export class CourseService {
  private http = inject(HttpClient);

  getCourses(classification: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${ENDPOINTS.course}/${classification}`);
  }
}
