import { type } from "arktype";

export const principalSchema = type({
    username: "string",
    firstname: "string",
    lastname: "string",
    fullname: "string",
    lang: "string",
    userid: "number",
    siteurl: "string.url",
    userpictureurl: "string.url",
    userissiteadmin: "boolean",
    sitename: "string"
});

export type Principal = typeof principalSchema.infer;
