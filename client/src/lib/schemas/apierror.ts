import { type } from 'arktype';

export const apiErrorSchema = type({
    timestamp: "string",
    status: "number",
    error: "string",
    message: "string",
    path: "string"
});

export type ApiError = typeof apiErrorSchema.infer;