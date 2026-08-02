<script lang="ts">
  import { goto } from "$app/navigation";
  import {
    TextInput,
    PasswordInput,
    Checkbox,
    Button,
    InlineNotification,
    Tile,
    Stack,
  } from "carbon-components-svelte";
  import { Reset } from "carbon-icons-svelte";
  import { session } from "$lib/client/session.svelte";
  import { hosts, loginForm } from "./login";

  const { form, errors, message, enhance, reset } = loginForm;

  const isInsecure = $form.host.startsWith("http://");

  $effect(() => {
    if (session.principal) {
      goto("#/dashboard");
    }
  });
</script>

{#if !session.principal}
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

        <form method="POST" use:enhance>
          <Stack gap={5}>
            <TextInput
              id="host"
              name="host"
              labelText="Host"
              placeholder="https://www.moodle.com"
              invalid={!!$errors.host}
              invalidText={$errors.host?.[0]}
              bind:value={$form.host}
              required
              warn={isInsecure}
              warnText="Esta conexión no es segura. Las credenciales pueden verse comprometidas."
              list="hosts"
            />

            <datalist id="hosts">
              {#each hosts as host}
                <option value={host}></option>
              {/each}
            </datalist>

            <TextInput
              id="username"
              name="username"
              labelText="Usuario"
              placeholder="nombre_de_usuario"
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
                labelText="Recomendar host"
                name="rememberHost"
                bind:checked={$form.rememberHost}
              />
              <Checkbox
                labelText="Recomendar usuario"
                name="rememberUsername"
                bind:checked={$form.rememberUsername}
              />
              <Checkbox
                labelText="Modo sin conexión"
                name="offlineMode"
                bind:checked={$form.offlineMode}
              />
            </Stack>

            <Stack gap={1} orientation="horizontal">
              <Button type="submit" kind="primary" style="flex: 1;"
                >Conectar</Button
              >
              <Button
                type="button"
                kind="secondary"
                icon={Reset}
                iconDescription="Resetear a valores inciales"
                onclick={() => reset()}
              />
            </Stack>
          </Stack>
        </form>

        {#if $message}
          <InlineNotification
            kind="error"
            title={$message.title}
            subtitle={$message.detail}
            hideCloseButton
          />
        {/if}
      </Stack>
    </Tile>
  </div>
{/if}
