import { z } from "zod";

export const loginSchema = z.object({
    host: z.url("Debe ser una URL válida"),
    username: z.string().min(1, "El nombre de usuario es obligatorio"),
    password: z.string().min(1, "La contraseña es obligatoria"),
    rememberHost: z.boolean().default(true),
    rememberUsername: z.boolean().default(true)
})

export type LoginSchema = z.infer<typeof loginSchema>
