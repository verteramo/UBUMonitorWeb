import { isDevMode } from '@angular/core';
import { SiteConfig } from '@core/models/site-config';
import { cleanUrl, getURIParam } from '@core/utils/string.utils';
import { environment as env } from '@env/environment';
import { signalStore, withMethods } from '@ngrx/signals';
import { MD5 } from 'crypto-js';

const PASSPORT_KEY = 'passport';

export const LoginPassportStore = signalStore(
  { providedIn: 'root' },
  withMethods(() => ({
    /**
     * Genera la URL de lanzamiento parametrizada con passport y scheme.
     *
     * @param siteConfig Configuración pública del sitio.
     * @returns URL parametrizada con passport y scheme.
     */
    getLaunchUrl(siteConfig: SiteConfig): string {
      const uuid = crypto.randomUUID();

      localStorage.setItem(
        PASSPORT_KEY,
        JSON.stringify({ site: cleanUrl(siteConfig.wwwRootSecure), uuid }),
      );

      const launchUrl = (
        siteConfig.loginLaunchUrl +
        '?service=moodle_mobile_app' +
        `&passport=${uuid}` +
        `&urlscheme=${encodeURIComponent(env.scheme)}`
      );

      return launchUrl;
    },

    /**
     * Decodifica el deep link devuelto por Moodle y extrae los tokens necesarios para iniciar sesión.
     *
     * El 'privatetoken' puede estar ausente:
     * https://github.com/moodle/moodle/blob/main/public/admin/tool/mobile/launch.php#L93
     *
     * @param uri URL capturada por la PWA (ej: web+umw://token=QmFzZTY0...)
     * @returns Objeto con token y token privado extraídos.
     *
     * @see https://github.com/moodle/moodle/blob/main/public/admin/tool/mobile/launch.php#L100
     */
    parseToken(uri: string) {
      try {
        /*
         * Cálculo de hash correspondiente a la URL del sitio y UUID almacenados.
         *
         * El 'passport' es una medida de seguridad que se puede verificar:
         * https://github.com/moodle/moodle/blob/main/public/admin/tool/mobile/launch.php#L100
         */
        const passport = localStorage.getItem(PASSPORT_KEY);

        if (!passport) {
          throw new Error('No SSO passport found in storage. Context lost.');
        }

        const { site, uuid } = JSON.parse(passport);
        const expectedHash = MD5(site + uuid).toString();

        const encodedToken = getURIParam(uri, 'token');
        const decodedToken = encodedToken && atob(encodedToken);
        const splitToken = decodedToken && decodedToken.split(':::');

        if (!splitToken || splitToken.length < 2) {
          throw new Error('Malformed SSO token');
        }

        const [hash, token, privateToken] = splitToken;

        if (hash !== expectedHash) {
          if (isDevMode()) {
            console.error(`Expected Hash: ${expectedHash}, Received: ${hash}`);
          }
          throw new Error('SSO Token not requested by self');
        }

        return { site: site as string, token, privateToken };
      } finally {
        localStorage.removeItem(PASSPORT_KEY);
      }
    },
  })),
);
