import { fail, redirect } from '@sveltejs/kit';
import { superValidate, message } from 'sveltekit-superforms';
import { zod4 } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { loginSchema } from '$lib/schemas/login';
import { getPreferences, saveAuth } from '$lib/server/cookies';

export const load: PageServerLoad = async ({ cookies }) => {
  const prefs = getPreferences(cookies);

  const form = await superValidate(
    {
      host: prefs.host,
      username: prefs.username,
      rememberHost: prefs.rememberHost,
      rememberUsername: prefs.rememberUsername,
      password: ''
    },
    zod4(loginSchema),
    { errors: false }
  );

  return { form };
};

export const actions: Actions = {
  default: async ({ request, cookies }) => {
    const form = await superValidate(request, zod4(loginSchema));

    if (!form.valid) {
      return fail(400, { form });
    }

    try {
      const response = await fetch('http://localhost:8080/api/token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Moodle-Host': form.data.host,
          'Accept-Language': request.headers.get('accept-language') || 'es'
        },
        body: JSON.stringify({
          username: form.data.username,
          password: form.data.password
        })
      });

      const resData = await response.json();

      if (!response.ok) {
        return message(form, resData.message || 'Error de autenticación', { status: response.status });
      }

      saveAuth(cookies, resData.token, form.data);
    } catch {
      return message(form, 'No se pudo conectar con el servidor de autenticación', { status: 500 });
    }

    redirect(303, '/dashboard');
  }
};