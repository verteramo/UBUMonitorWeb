/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type Module = {
  id: number;
  url: string | null;
  name: string;
  type: string;
  plural: string;
  picture: string;
  purpose: string;
  completion: number;
  isVisible: boolean;
  isUserVisible: boolean;
  sinceTs: number | null;
  untilTs: number | null;
};
