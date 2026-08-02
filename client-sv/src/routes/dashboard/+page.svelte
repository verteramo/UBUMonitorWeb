<script lang="ts">
    import { goto } from "$app/navigation";
    import { session } from "$lib/client/session.svelte";
    import {
        Header,
        HeaderUtilities,
        HeaderAction,
        HeaderPanelLinks,
        HeaderPanelLink,
        SideNav,
        SideNavItems,
        SideNavLink,
        Content,
        SkipToContent,
    } from "carbon-components-svelte";
    import { UserAvatar, Logout, Home, Settings } from "carbon-icons-svelte";

    let isSideNavOpen = $state(false);
    let isProfileOpen = $state(false);

    $effect(() => {
        if (!session.principal) {
            goto("/#/login");
        }
    });

    async function handleLogout() {
        session.logout();
        await goto("/#/login");
    }
</script>

{#if session.principal}
    <Header company="Mi Aplicación" platformName="UBUMonitorWeb" bind:isSideNavOpen>
        <svelte:fragment slot="skip-to-content">
            <SkipToContent />
        </svelte:fragment>

        <HeaderUtilities>
            <span
                style="display: flex; align-items: center; padding: 0 1rem; font-size: 0.875rem;"
            >
                {session.principal?.fullname}
            </span>

            <HeaderAction bind:isOpen={isProfileOpen} iconDescription="Perfil">
                <div
                    slot="icon"
                    style="display: flex; width: 24px; height: 24px;"
                >
                    {#if session.principal?.userpictureurl}
                        <img
                            src={session.principal.userpictureurl}
                            alt={session.principal.fullname}
                            style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;"
                        />
                    {:else}
                        <UserAvatar size={24} />
                    {/if}
                </div>

                <HeaderPanelLinks>
                    {#if session.principal?.siteurl && session.principal?.sitename}
                        <HeaderPanelLink
                            href={session.principal.siteurl}
                            target="_blank"
                        >
                            {session.principal.sitename}
                        </HeaderPanelLink>
                    {/if}
                    <HeaderPanelLink onclick={handleLogout}>
                        <span
                            style="display: flex; align-items: center; gap: 0.5rem;"
                        >
                            <Logout size={16} /> Cerrar sesión
                        </span>
                    </HeaderPanelLink>
                </HeaderPanelLinks>
            </HeaderAction>
        </HeaderUtilities>
    </Header>

    <SideNav bind:isOpen={isSideNavOpen}>
        <SideNavItems>
            <SideNavLink icon={Home} text="Inicio" href="#/dashboard" />
            <SideNavLink
                icon={Settings}
                text="Configuración"
                href="#/dashboard/settings"
            />
        </SideNavItems>
    </SideNav>

    <Content>
        <div style="padding: 1rem;">
            <h1>Panel de control</h1>
            <p>Bienvenido, {session.principal?.username || "Usuario"}.</p>
        </div>
    </Content>
{/if}
