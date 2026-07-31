import ky from 'ky';

export const api = ky.extend({
    hooks: {
        beforeRequest: [
            ({ request }) => {
                const host = localStorage.getItem('host');
                const language = localStorage.getItem('language') || navigator.language;

                if (host && !request.headers.has('Moodle-Host')) {
                    request.headers.set('Moodle-Host', host);
                }

                if (language) {
                    request.headers.set('Accept-Language', language);
                }
            }
        ]
    }
});
