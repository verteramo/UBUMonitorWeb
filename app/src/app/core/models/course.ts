/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type Course = {
  id: number;
  name: string;
  picture: string;
  category: string;
  isStarred: boolean;
  sinceTs: number | null;
  untilTs: number | null;
};
