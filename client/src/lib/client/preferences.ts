import type { LoginSchema } from '$lib/schemas/login';

export function getPreferences() {
    if (typeof window === 'undefined') {
        return { host: '', username: '', rememberHost: true, rememberUsername: true };
    }

    return {
        host: localStorage.getItem('host') ?? '',
        username: localStorage.getItem('username') ?? '',
        rememberHost: localStorage.getItem('remember_host') !== 'false',
        rememberUsername: localStorage.getItem('remember_username') !== 'false'
    };
}

export function savePreferences(input: LoginSchema) {
    if (typeof window === 'undefined') return;

    if (input.rememberHost) {
        localStorage.setItem('host', input.host);
    } else {
        localStorage.removeItem('host');
    }
    localStorage.setItem('remember_host', String(input.rememberHost));

    if (input.rememberUsername) {
        localStorage.setItem('username', input.username);
    } else {
        localStorage.removeItem('username');
    }
    localStorage.setItem('remember_username', String(input.rememberUsername));
}