/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type Completion = {
  cmid: number;
  module: string;
  instance: number;
  state: number | null;
  completedTs: number | null;
  tracking: number;
  overrideBy: number | null;
  isEnabled: boolean;
  isValueUsed: boolean;
  isAutomatic: boolean;
  isTrackedUser: boolean;
  isUserVisible: boolean;
  isOverallComplete: boolean;
};
