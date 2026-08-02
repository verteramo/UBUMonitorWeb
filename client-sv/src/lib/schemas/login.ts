import { type } from "arktype";

export const loginSchema = type({
    host: "string.url",
    username: "string>0",
    password: "string>0",
    rememberHost: "boolean",
    rememberUsername: "boolean",
    offlineMode: "boolean"
});

export type Login = typeof loginSchema.infer;