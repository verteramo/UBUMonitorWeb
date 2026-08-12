import { inject, Service } from '@angular/core';
import { SESSION_STORAGE } from '@core/services/storage.service';
import { AbstractStore } from './abstract-store';

@Service()
export class HostStore extends AbstractStore<string | null> {
  constructor() {
    super('host', inject(SESSION_STORAGE), null);
  }
}
