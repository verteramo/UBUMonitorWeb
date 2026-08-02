import type { Principal } from "$lib/schemas/principal";

let currentPrincipal = $state<Principal | null>(null);
let currentPassword = $state<string | null>(null);

export const session = {
  get principal() {
    return currentPrincipal;
  },

  get password() {
    return currentPassword;
  },

  login(principal: Principal, password: string) {
    currentPrincipal = principal;
    currentPassword = password;
  },

  clear() {
    currentPrincipal = null;
    currentPassword = null;
  }
};
