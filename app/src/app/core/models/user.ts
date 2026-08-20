export interface User {
  id: number;
  fullname: string;
  username?: string;
  firstname?: string;
  lastname?: string;
  initials?: string;
  email?: string;
  address?: string;
  phone1?: string;
  phone2?: string;
  department?: string;
  institution?: string;
  idnumber?: string;
  interests?: string;
  firstaccess?: Date;
  lastaccess?: Date;
  lastcourseaccess?: Date;
  description?: string;
  descriptionformat?: number;
  city?: string;
  country?: string;
  profileimageurl?: string;
  customfields: Map<string, string>;
  groups: string[];
  roles: string[];
  preferences: Map<string, string>;
  enrolledcourses: string[];
}
