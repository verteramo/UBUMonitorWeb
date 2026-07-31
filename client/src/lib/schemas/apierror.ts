import { type } from 'arktype';

export const problemDetailSchema = type({
    title: "string",
    status: "number",
    detail: "string",
});

export type ProblemDetail = typeof problemDetailSchema.infer;
