import CryptoJS from "crypto-js";
import { type User } from "$lib/schemas/user";

let currentUser = $state<User | null>(null);
let currentPassword = $state<string | null>(null);

export const session = {
  get user() {
    // Si no está en memoria pero existe la clave y hay datos cifrados en localStorage
    if (!currentUser && currentPassword && typeof window !== "undefined") {
      const encryptedData = localStorage.getItem("user_data");
      if (encryptedData) {
        try {
          const bytes = CryptoJS.AES.decrypt(encryptedData, currentPassword);
          currentUser = JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
        } catch (e) {
          this.clear();
        }
      }
    }
    return currentUser;
  },
  
  login(userData: User, password: string) {
    currentUser = userData;
    currentPassword = password;
    
    if (typeof window !== "undefined") {
      // Ciframos los datos del usuario usando la contraseña como clave simétrica
      const encryptedData = CryptoJS.AES.encrypt(JSON.stringify(userData), password).toString();
      localStorage.setItem("user_data", encryptedData);
    }
  },
  
  clear() {
    currentUser = null;
    currentPassword = null;
    if (typeof window !== "undefined") {
      localStorage.removeItem("user_data");
    }
  }
};
