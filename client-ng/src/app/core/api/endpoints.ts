import { environment } from '@env/environment';

const API_URL = environment.apiUrl;

export const ENDPOINTS = {
  auth: {
    login: `${API_URL}/auth/login`,
    logout: `${API_URL}/auth/logout`,
  },
  course: `${API_URL}/course`,
  users: `${API_URL}/users`,
} as const;
