/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Course } from '@core/models/course';
import { environment as env } from '@env/environment';
import { Observable, of, shareReplay } from 'rxjs';

/** Clasificación de cursos. */
export type CourseClassification = 'all' | 'starred' | 'recent' | 'inprogress' | 'future' | 'past';

/** Servicio de cursos. */
@Service()
export class CourseService {
  /** Cliente HTTP */
  #http = inject(HttpClient);

  /** Caché de cursos clasificados. */
  #classifiedCourses = new Map<CourseClassification, Observable<Course[]>>();

  /**
   * Solicita la lista de cursos de una clasificación determinada.
   *
   * @param classification Clasificación solicitada.
   * @returns Lista de cursos de la clasificación solicitada.
   */
  getCourses(classification: CourseClassification): Observable<Course[]> {
    /*
     * Se almacenan los cursos en memoría para minimizar
     * solicitudes costosas al servicio de Moodle.
     */
    if (this.#classifiedCourses.has(classification)) {
      return this.#classifiedCourses.get(classification) as Observable<Course[]>;
    }

    // Construcción del endpoint con la clasificación solicitada
    const endpoint = `${env.endpoints.courses}/${classification}`;

    const courses$ = this.#http.get<Course[]>(endpoint).pipe(
      // Almacena en memoria el último valor emitido
      shareReplay(1),
    );

    this.#classifiedCourses.set(classification, courses$);

    return courses$;
  }

  getContents(id: number): Observable<any[]> {
    return of([]);
  }
}
