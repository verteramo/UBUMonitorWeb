import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Course } from '@core/models/course';
import { endpoints } from '@core/services/endpoints';
import { Observable, shareReplay } from 'rxjs';

/**
 * Clasificación de cursos.
 */
export const CourseClassifications = ['all', 'starred', 'recent', 'inprogress', 'future', 'past'];

/**
 * Tipo para restringir la clasificación solicitada al servicio.
 */
export type CourseClassification = (typeof CourseClassifications)[number];

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

    const courses$ = this.http.get<Course[]>(`${endpoints.courses}/${classification}`).pipe(
      // Almacena en memoria el último valor emitido
      shareReplay(1),
    );

    this.classifiedCourses.set(classification, courses$);

    return courses$;
  }
}
