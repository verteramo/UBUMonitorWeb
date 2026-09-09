/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type Event = {
  id: number;
  name: string;
  description: string | null;
  format: number;
  courseId: number;
  categoryId: number | null;
  groupId: number | null;
  userId: number | null;
  repeatId: number | null;
  module: string | null;
  instance: number | null;
  type: string;
  startTs: number;
  durationTs: number;
  isVisible: boolean;
  uuid: string | null;
  sequence: number;
  modifiedTs: number;
  subscriptionId: number | null;
};
