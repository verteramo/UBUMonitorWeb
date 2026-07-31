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
        if (!session.user) {
            goto("/#/login");
        }
    });

    async function handleLogout() {
        session.logout();
        await goto("/#/login");
    }
</script>

{#if session.user}
    <Header company="Mi Aplicación" platformName="Dashboard" bind:isSideNavOpen>
        <svelte:fragment slot="skip-to-content">
            <SkipToContent />
        </svelte:fragment>

        <HeaderUtilities>
            <span
                style="display: flex; align-items: center; padding: 0 1rem; font-size: 0.875rem;"
            >
                {session.user?.fullname}
            </span>

            <HeaderAction bind:isOpen={isProfileOpen} iconDescription="Perfil">
                <div
                    slot="icon"
                    style="display: flex; width: 24px; height: 24px;"
                >
                    {#if session.user?.userpictureurl}
                        <img
                            src={session.user.userpictureurl}
                            alt={session.user.fullname}
                            style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;"
                        />
                    {:else}
                        <UserAvatar size={24} />
                    {/if}
                </div>

                <HeaderPanelLinks>
                    {#if session.user?.siteurl && session.user?.sitename}
                        <HeaderPanelLink
                            href={session.user.siteurl}
                            target="_blank"
                        >
                            {session.user.sitename}
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
            <p>Bienvenido, {session.user?.username || "Usuario"}.</p>
        </div>
    </Content>
{/if}
