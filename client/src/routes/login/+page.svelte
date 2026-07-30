<script lang="ts">
  import { goto } from "$app/navigation";
  import { superForm, defaults } from "sveltekit-superforms";
  import { arktype } from "sveltekit-superforms/adapters";
  import {
    TextInput,
    PasswordInput,
    Checkbox,
    Button,
    InlineNotification,
    Tile,
    Stack,
  } from "carbon-components-svelte";
  import { Clean } from "carbon-icons-svelte";
  import { loginSchema } from "$lib/schemas/login";
  import { getPreferences, savePreferences } from "$lib/client/preferences";
  import { api } from "$lib/client/api";
  import { isHTTPError } from "ky";
  import { type ApiError } from "$lib/schemas/apierror";

  const { form, errors, message, enhance, reset } = superForm(
    defaults(getPreferences(), arktype(loginSchema)),
    {
      SPA: true,
      validators: arktype(loginSchema),
      async onUpdate({ form, cancel }) {
        if (form.valid) {
          try {
            await api.post("/api/token", {
              headers: {
                "Moodle-Host": String(form.data.host),
              },
              json: {
                username: form.data.username,
                password: form.data.password,
              },
            });

            savePreferences(form.data);
            goto("#/dashboard");
          } catch (error) {
            // 3. Recuperar la asignación del mensaje de error
            if (isHTTPError<ApiError>(error)) {
              const data = error.data as ApiError;
              $message = data.message;
            }

            cancel();
          }
        } else {
          cancel();
        }
      },
    },
  );
</script>

<div
  style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background-color: var(--cds-background); padding: 1rem;"
>
  <Tile style="width: 100%; max-width: 400px;">
    <Stack gap={6}>
      <div style="text-align: center;">
        <img
          src="/logo1.png"
          alt="Logo"
          style="height: 48px; margin-bottom: 0.5rem;"
        />
        <h2>Iniciar sesión</h2>
        <p>Conexión a la plataforma</p>
      </div>

      {#if $message}
        <InlineNotification
          kind="error"
          title="Error"
          subtitle={$message}
          hideCloseButton
        />
      {/if}

      <form method="POST" use:enhance>
        <Stack gap={5}>
          <TextInput
            id="host"
            name="host"
            labelText="Host"
            placeholder="https://moodle.ejemplo.com"
            bind:value={$form.host}
            invalid={!!$errors.host}
            invalidText={$errors.host?.[0]}
            required
          />

          <TextInput
            id="username"
            name="username"
            labelText="Usuario"
            placeholder="tu_usuario"
            bind:value={$form.username}
            invalid={!!$errors.username}
            invalidText={$errors.username?.[0]}
            required
          />

          <PasswordInput
            id="password"
            name="password"
            labelText="Contraseña"
            bind:value={$form.password}
            invalid={!!$errors.password}
            invalidText={$errors.password?.[0]}
            required
          />

          <Stack gap={2}>
            <Checkbox
              labelText="Guardar host"
              name="rememberHost"
              bind:checked={$form.rememberHost}
            />
            <Checkbox
              labelText="Guardar usuario"
              name="rememberUsername"
              bind:checked={$form.rememberUsername}
            />
          </Stack>

          <div style="display: flex; gap: 0.5rem;">
            <Button type="submit" kind="primary" style="flex: 1;"
              >Conectar</Button
            >
            <Button
              type="button"
              kind="ghost"
              icon={Clean}
              iconDescription="Borrar campos"
              onclick={() => reset()}
            />
          </div>
        </Stack>
      </form>
    </Stack>
  </Tile>
</div>
