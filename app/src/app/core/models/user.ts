export interface MoodleUser {
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
  firstaccess?: number;
  lastaccess?: number;
  lastcourseaccess?: number;
  description?: string;
  descriptionformat?: number;
  city?: string;
  country?: string;
  profileimageurlsmall?: string;
  profileimageurl?: string;
  customfields?: CustomField[];
  groups?: Group[];
  roles?: Role[];
  preferences?: Preference[];
  enrolledcourses?: EnrolledCourse[];
}

export interface CustomField {
  type: string;
  value: string;
  name: string;
  shortname: string;
}

export interface Group {
  id: number;
  name: string;
  description: string;
  descriptionformat: number;
}

export interface Role {
  roleid: number;
  name: string;
  shortname: string;
  sortorder: number;
}

export interface Preference {
  name: string;
  value: string;
}

export interface EnrolledCourse {
  id: number;
  fullname: string;
  shortname: string;
}

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
  profileimageurlsmall?: string;
  profileimageurl?: string;
  customfields?: CustomField[];
  groups?: Group[];
  roles?: Role[];
  preferences?: Preference[];
  enrolledcourses?: EnrolledCourse[];
}
