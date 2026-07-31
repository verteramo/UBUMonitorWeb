import { type } from "arktype";

export const userSchema = type({
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

export type User = typeof userSchema.infer;
