import { goto } from "$app/navigation";
import { superForm, defaults } from "sveltekit-superforms";
import { arktype } from "sveltekit-superforms/adapters";
import { loginSchema } from "$lib/schemas/login";
import { getPreferences, savePreferences } from "$lib/client/preferences";
import { api } from "$lib/client/api";
import { isHTTPError } from "ky";
import type { ProblemDetail } from "$lib/schemas/apierror";
import { principalSchema } from "$lib/schemas/principal";
import { session } from "$lib/client/session.svelte";

const initialPrefs = getPreferences();

export const hosts = initialPrefs.hosts;

export const loginForm = superForm(
    defaults({ ...initialPrefs, offlineMode: false }, arktype(loginSchema)),
    {
        SPA: true,
        validators: arktype(loginSchema),
        async onUpdate({ form, cancel }) {
            if (form.valid) {
                const response = await api.post("/auth/login", {
                    headers: {
                        "Moodle-Host": form.data.host,
                    },
                    body: new URLSearchParams({
                        username: form.data.username,
                        password: form.data.password,
                    }),
                }).json();

                const principal = principalSchema.assert(response);
                session.login(principal, form.data.password);
                savePreferences(form.data);

                await goto("#/dashboard");
            } else {
                cancel();
            }
        },
        onError({ result: { error } }) {
            console.log("Result error", error);
            if (isHTTPError<ProblemDetail>(error)) {
                loginForm.message.set(error.data);
            } else {
                loginForm.message.set({
                    title: "Error desconocido",
                    detail: "Ha ocurrido un error desconocido. Por favor, inténtalo de nuevo.",
                });
            }
        },
    }
);
