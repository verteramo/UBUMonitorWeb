import { environment } from '../../../environments/environment';

const API = environment.apiUrl;

export const ENDPOINTS = {
  auth: {
    login: `${API}/auth/login`,
    logout: `${API}/auth/logout`,
  },
  users: `${API}/users`,
} as const;
