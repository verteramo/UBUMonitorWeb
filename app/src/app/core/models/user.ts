export interface User {
  id: number;
  username?: string;
  email?: string;
  fullName: string;
  picture?: string;
  firstAccess?: number;
  lastAccess?: number;
  lastCourseAccess?: number;
  country?: string;
  phones: string[];
  groups: string[];
  roles: string[];
  courses: string[];
}
