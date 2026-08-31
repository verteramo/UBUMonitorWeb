/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Course } from '@core/models/course';
import { Section } from '@core/models/section';
import { User } from '@core/models/user';
import { environment as env } from '@env/environment';
import { Observable, shareReplay } from 'rxjs';

/** Clasificación de cursos. */
export type CourseClassification = 'all' | 'starred' | 'recent' | 'inprogress' | 'future' | 'past';

/** Tipo para el diccionario de cursos en memoria. */
type CourseCache = Record<CourseClassification, Observable<Course[]>>;

/** Servicio de cursos. */
@Service()
export class CourseService {
  private http = inject(HttpClient);
  private cache: Partial<CourseCache> = {};

  /**
   * Obtiene la lista de cursos de una clasificación determinada.
   *
   * @param classification Clasificación solicitada.
   * @returns Lista de cursos.
   */
  getCourses(classification: CourseClassification): Observable<Course[]> {
    return (this.cache[classification] ??= this.http
      .get<Course[]>(`${env.endpoints.courses}/${classification}`)
      .pipe(shareReplay(1)));
  }

  /**
   * Obtiene la lista de usuarios de un curso determinado.
   *
   * @param id ID del curso.
   * @returns Lista de usuarios del curso.
   */
  getUsers(id: number): Observable<User[]> {
    const endpoint = `${env.endpoints.users}/${id}`;
    return this.http.get<User[]>(endpoint);
  }

  /**
   * Obtiene la lista de secciones de un curso determinado.
   * @param id ID del curso.
   * @returns Lista de secciones del curso.
   */
  getSections(id: number): Observable<Section[]> {
    const endpoint = `${env.endpoints.sections}/${id}`;
    return this.http.get<Section[]>(endpoint);
  }
}
