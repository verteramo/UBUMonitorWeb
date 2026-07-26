import type { Cookies } from '@sveltejs/kit';
import type { LoginSchema } from '$lib/schemas/login';

export function getPreferences(cookies: Cookies) {
    return {
        host: cookies.get('host') ?? '',
        username: cookies.get('username') ?? '',
        rememberHost: cookies.get('remember_host') !== 'false',
        rememberUsername: cookies.get('remember_username') !== 'false'
    };
}

export function saveAuth(cookies: Cookies, token: string, input: LoginSchema) {
    cookies.set('token', token, { path: '/', httpOnly: true, sameSite: 'lax', maxAge: 604800 });

    if (input.rememberHost) {
        cookies.set('host', input.host, { path: '/', maxAge: 2592000 });
    } else {
        cookies.delete('host', { path: '/' });
    }

    cookies.set('remember_host', String(input.rememberHost), { path: '/' });

    if (input.rememberUsername) {
        cookies.set('username', input.username, { path: '/', maxAge: 2592000 });
    } else {
        cookies.delete('username', { path: '/' });
    }
    
    cookies.set('remember_username', String(input.rememberUsername), { path: '/' });
}
