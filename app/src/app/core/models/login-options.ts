/**
 * Define los tipos de inicio de sesión soportados o requeridos por el servidor Moodle.
 *
 * https://github.com/moodle/moodle/blob/main/public/admin/tool/mobile/classes/api.php
 */
export enum LoginType {
  /** Inicio de sesión clásico enviando credenciales */
  APP = 1,

  /** Delegación del login abriendo el navegador */
  BROWSER = 2,

  /** Delegación del login utilizando un navegador integrado/WebView */
  EMBEDDED_BROWSER = 3,
}

export type LoginOptions = {
  siteName: string;
  siteLogo: string | null;
  loginType: LoginType;
  loginUrl: string;
  loginInstructions: string | null,
  maintenanceMessage: string;
  isMaintenanceMode: boolean;
  isLoginFormEnabled: boolean;
  isLoginByEmailEnabled: boolean;
  isMobileServiceEnabled: boolean;
  isMfaEnabled: boolean;
  idProviders: {
    url: string;
    name: string;
    icon: string;
  }[];
};
