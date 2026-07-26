<script lang="ts">
  import { goto } from "$app/navigation";
  import { superForm } from "sveltekit-superforms";
  import { zod4 } from "sveltekit-superforms/adapters";
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
  import type { PageProps } from "./$types";
  import { loginSchema } from "$lib/schemas/login";
  import { savePreferences } from "$lib/client/preferences";
  import { apiFetch } from "$lib/client/api";

  let { data }: PageProps = $props();

  const { form, errors, message, enhance, reset } = superForm(data.form, {
    SPA: true,
    validators: zod4(loginSchema),
    async onUpdate({ form, cancel }) {
      // Si la validación de Zod falla, detenemos el envío
      if (!form.valid) return cancel();

      try {
        const response = await apiFetch("/api/token", {
          method: "POST",
          headers: {
            "Moodle-Host": form.data.host,
          },
          body: JSON.stringify({
            username: form.data.username,
            password: form.data.password,
          }),
        });

        if (!response.ok) {
          // Extraemos el JSON con el mensaje de error de Spring Boot
          const errorData = await response.json().catch(() => ({}));

          // Asignamos el error directamente a la variable reactiva del mensaje
          $message = errorData.message || "Error de autenticación";
          return cancel();
        }

        // Si es 200 OK, el navegador ya ha guardado la cookie HttpOnly
        savePreferences(form.data);
        goto("/dashboard");
      } catch (e) {
        $message = "No se pudo conectar con el servidor";
        cancel();
      }
    },
  });
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
          title="Error:"
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
