export interface ApiError {
  userCode: string;
  timestamp: string;
  userMessage: string;
  customMessage?: string;
  httpStatusCode: number;
}
