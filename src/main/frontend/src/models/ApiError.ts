export interface ApiError {
    userCode: string;
    timestamp: string;
    userMessage: string;
    httpStatusCode: number;
}