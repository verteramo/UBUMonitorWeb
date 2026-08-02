import type { Login } from '$lib/schemas/login';

const PREFERENCES_KEY = 'ubumonitorweb_preferences';

export interface Preferences {
    host: string;
    hosts: string[];
    username: string;
    rememberHost: boolean;
    rememberUsername: boolean;
}

const defaultPrefs: Preferences = {
    host: '',
    hosts: [],
    username: '',
    rememberHost: true,
    rememberUsername: true
};

export const getPreferences = (): Preferences => {
    const prefs = localStorage.getItem(PREFERENCES_KEY);
    if (prefs) {
        try {
            return { ...defaultPrefs, ...JSON.parse(prefs) };
        }
        catch {
            return defaultPrefs;
        }
    }
    return defaultPrefs;
};

export function savePreferences(input: Login) {
    const currentPrefs = getPreferences();
    let hosts = currentPrefs.hosts;

    if (input.rememberHost && !hosts.includes(input.host)) {
        hosts = [input.host, ...hosts];
    }

    const prefs: Preferences = {
        host: input.rememberHost ? input.host : '',
        hosts,
        username: input.rememberUsername ? input.username : '',
        rememberHost: input.rememberHost,
        rememberUsername: input.rememberUsername
    };

    localStorage.setItem(PREFERENCES_KEY, JSON.stringify(prefs));
}
