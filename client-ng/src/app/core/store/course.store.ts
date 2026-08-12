import { inject, Service } from '@angular/core';
import { Course } from '@core/models/course';
import { SESSION_STORAGE } from '@core/services/storage.service';
import { AbstractStore } from './abstract-store';

@Service()
export class CourseStore extends AbstractStore<Course | null> {
  constructor() {
    super('course', inject(SESSION_STORAGE), null);
  }
}
