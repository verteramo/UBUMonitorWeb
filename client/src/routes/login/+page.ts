import { superValidate } from 'sveltekit-superforms';
import { zod4 } from 'sveltekit-superforms/adapters';
import type { PageLoad } from './$types';
import { loginSchema } from '$lib/schemas/login';
import { getPreferences } from '$lib/client/preferences';

export const load: PageLoad = async () => {
  const prefs = getPreferences();

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