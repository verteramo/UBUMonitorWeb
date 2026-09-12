/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type Principal = {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  fullName: string;
  picture: string | null;
  language: string;
  timezone: string;
  isAdmin: boolean;
  siteUrl: string;
  siteName: string;
  siteVersion: string | null;
  siteRelease: string | null;
  siteTimezone: string;
};
