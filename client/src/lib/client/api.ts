export async function apiFetch(endpoint: string, options: RequestInit = {}): Promise<Response> {
    const headers = new Headers(options.headers);

    // Inyectamos el Accept-Language por defecto si no se ha establecido uno manualmente
    if (!headers.has('Accept-Language')) {
        headers.set('Accept-Language', 'es');
    }

    // Puedes aprovechar para forzar que todas tus llamadas a la API incluyan Content-Type JSON
    if (!headers.has('Content-Type') && options.body) {
        headers.set('Content-Type', 'application/json');
    }

    return fetch(endpoint, {
        ...options,
        headers
    });
}