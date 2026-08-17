export enum Format {
  MOODLE = 0,
  HTML = 1,
  PLAIN = 2,
  MARKDOWN = 4,
}

export interface Category {
  id: number;
  name: string;
  description: string;
  descriptionformat: Format;
  path: string;
}

export interface ApiCourse {
  id: number;
  fullname: string;
  summary: string;
  summaryformat: Format;
  courseimage: string;
  isfavourite: boolean;
  startdate: number;
  enddate: number;
  category: Category;
}

export type Course = Omit<ApiCourse, 'startdate'  | 'enddate'> & {
  startdate: Date;
  enddate: Date;
};
