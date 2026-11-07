/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type LoginStatus = 'ENABLED' | 'DISABLED' | 'MAINTENANCE';

export type LoginMode = 'CREDENTIALS' | 'SSO' | 'ALL';

export type SiteConfig = {
  siteName: string;
  siteLogo: string | null;
  loginStatus: LoginStatus;
  loginMode: LoginMode | null;
  loginUrl: string | null;
  loginInstructions: string | null;
  maintenanceMessage: string | null;
};
