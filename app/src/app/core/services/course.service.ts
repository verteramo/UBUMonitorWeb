/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Completion } from '@core/models/completion';
import { Course } from '@core/models/course';
import { Grade } from '@core/models/grade';
import { Section } from '@core/models/section';
import { User } from '@core/models/user';
import { environment as env } from '@env/environment';
import { Observable } from 'rxjs';

/**
 * Clasificaciones de cursos.
 */
export type CourseClassification = 'all' | 'starred' | 'recent' | 'inprogress' | 'future' | 'past';

/**
 * Servicio de cursos.
 */
@Service()
export class CourseService {
  private http = inject(HttpClient);

  /**
   * Obtiene un observable con la información solicitada.
   *
   * @param endpoint Subruta.
   * @returns Observable con la información solicitada.
   */
  private getData<T>(endpoint: string): Observable<T> {
    return this.http.get<T>(`${env.endpoints.courses}/${endpoint}`);
  }

  /**
   * Obtiene la lista de cursos de una clasificación determinada.
   *
   * @param classification Clasificación solicitada.
   * @returns Lista de cursos.
   */
  getCourses(classification: CourseClassification): Observable<Course[]> {
    return this.getData<Course[]>(classification);
  }

  /**
   * Obtiene la lista de usuarios de un curso determinado.
   *
   * @param id ID del curso.
   * @returns Lista de usuarios del curso.
   */
  getUsers(id: number): Observable<User[]> {
    return this.getData<User[]>(`${id}/users`);
  }

  /**
   * Obtiene la lista de secciones de un curso determinado.
   * @param id ID del curso.
   * @returns Lista de secciones del curso.
   */
  getSections(id: number): Observable<Section[]> {
    return this.getData<Section[]>(`${id}/sections`);
  }

  /**
   * Obtiene la lista de calificaciones de un curso determinado.
   * @param id ID del curso.
   * @returns Lista de calificaciones del curso.
   */
  getGrades(id: number): Observable<Grade[]> {
    return this.getData<Grade[]>(`${id}/grades`);
  }

  /**
   * Obtiene la lista de eventos de un curso determinado.
   * @param id ID del curso.
   * @returns Lista de eventos del curso.
   */
  getEvents(id: number): Observable<Event[]> {
    return this.getData<Event[]>(`${id}/events`);
  }

  /**
   * Obtiene el estado de finalización de actividades de un usuario en un curso determinado.
   * @param courseId ID del curso.
   * @param userId ID del usuario.
   * @returns Estado de finalización de actividades del usuario.
   */
  getCompletion(courseId: number, userId: number): Observable<Completion[]> {
    return this.getData<Completion[]>(`${courseId}/completion/${userId}`);
  }
}
