import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { ENDPOINTS } from '@core/api/endpoints';
import { ApiCourse, Course } from '@core/models/course';
import { map, Observable } from 'rxjs';

@Service()
export class CourseService {
  private http = inject(HttpClient);

  getCourses(classification: string): Observable<Course[]> {
    return this.http.get<ApiCourse[]>(`${ENDPOINTS.course}/${classification}`).pipe(
      // Mape de fechas de long a Date
      map((courses) => courses.map(course => ({
        ...course,
        startdate: new Date(course.startdate),
        enddate: new Date(course.enddate),
      }))
    ))
  }
}
