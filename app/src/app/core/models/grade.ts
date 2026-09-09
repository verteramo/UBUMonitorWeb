/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { GradeItem } from './grade-item';

export type Grade = {
  courseId: number;
  userId: number;
  grades: GradeItem[];
};
