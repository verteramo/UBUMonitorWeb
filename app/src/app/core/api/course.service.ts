import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { endpoints } from '@core/api/endpoints';
import { Course } from '@core/models/course';
import { map, Observable, shareReplay } from 'rxjs';

interface ApiCourse extends Omit<Course, 'startdate' | 'enddate'> {
  startdate: number;
  enddate: number;
}

/**
 * Clasificación de cursos.
 */
export const COURSE_CLASSIFICATION = ['all', 'starred', 'recent', 'inprogress', 'future', 'past'];

/**
 * Tipo para restringir la clasificación solicitada al servicio.
 */
export type CourseClassification = (typeof COURSE_CLASSIFICATION)[number];

/**
 * Servicio de cursos.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Service()
export class CourseService {
  /** Cliente HTTP */
  private http = inject(HttpClient);

  /** Caché de cursos clasificados. */
  private classifiedCourses = new Map<CourseClassification, Observable<Course[]>>();

  getCourses(classification: CourseClassification): Observable<Course[]> {
    if (this.classifiedCourses.has(classification)) {
      return this.classifiedCourses.get(classification) as Observable<Course[]>;
    }

    const courses$ = this.http.get<ApiCourse[]>(`${endpoints.courses}/${classification}`).pipe(
      // Almacena en memoria el último valor emitido
      shareReplay(1),
      // Mapeo de fechas de long a Date
      map((courses) =>
        courses.map((course) => ({
          ...course,
          startdate: new Date(course.startdate),
          enddate: new Date(course.enddate),
        })),
      ),
    );

    this.classifiedCourses.set(classification, courses$);

    return courses$;
  }
}
