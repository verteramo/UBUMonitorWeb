import { inject, Service } from '@angular/core';
import { SESSION_STORAGE } from '@core/services/storage.service';
import { AbstractStore } from './abstract-store';

export interface Principal {
  username: string;
  firstname: string;
  lastname: string;
  fullname: string;
  lang: string;
  userid: number;
  siteurl: string;
  userpictureurl: string;
  userissiteadmin: boolean;
  sitename: string;
  version: string;
  release: string;
}

@Service()
export class PrincipalStore extends AbstractStore<Principal | null> {
  constructor() {
    super('principal', inject(SESSION_STORAGE), null);
  }
}
