import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { endpoints } from '@core/api/endpoints';
import { ApiCourse, Course } from '@core/models/course';
import { map, Observable } from 'rxjs';

/**
 * Clasificación de cursos.
 */
export const COURSE_CLASSIFICATION = ['all', 'starred', 'recent', 'inprogress', 'future', 'past'];

/**
 * Tipos para restringir la clasificación solicitada al servicio.
 */
export type CourseClassification = (typeof COURSE_CLASSIFICATION)[number];

/**
 * Servicio de cursos.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class CourseService {
  private http = inject(HttpClient);

  getCourses(classification: CourseClassification): Observable<Course[]> {
    return this.http.get<ApiCourse[]>(`${endpoints.courses}/${classification}`).pipe(
      // Mapeo de fechas de long a Date
      map((courses) =>
        courses.map((course) => ({
          ...course,
          startdate: new Date(course.startdate),
          enddate: new Date(course.enddate),
        })),
      ),
    );
  }
}
