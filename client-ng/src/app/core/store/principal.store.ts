import { inject, Service } from '@angular/core';
import { Principal } from '@core/models/principal';
import { SESSION_STORAGE } from '@core/services/storage.service';
import { AbstractStore } from './abstract-store';

@Service()
export class PrincipalStore extends AbstractStore<Principal | null> {
  constructor() {
    super('principal', inject(SESSION_STORAGE), null);
  }
}
