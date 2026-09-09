/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type GradeItem = {
  id: number;
  cmid: number | null;
  name: string | null;
  type: string;
  module: string | null;
  instance: number;
  number: number | null;
  category: number | null;
  weight: number | null;
  value: number | null;
  minValue: number | null;
  maxValue: number | null;
  isUserLocked: boolean;
  isLocked: boolean;
  isHidden: boolean;
  isOverridden: boolean;
  needsUpdate: boolean;
  isHiddenByDate: boolean;
  submittedTs: number | null;
  gradedTs: number | null;
  status: string | null;
  feedback: string | null;
};
