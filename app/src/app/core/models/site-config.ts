/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */


export type LoginStatus = 'DISABLED' | 'MAINTENANCE' | 'CREDENTIALS' | 'SSO' | 'ALL';

export type SiteConfig = {
  siteName: string;
  siteLogo: string | null;
  loginStatus: LoginStatus | null;
  loginUrl: string | null;
  loginInstructions: string | null;
  maintenanceMessage: string | null;
};
