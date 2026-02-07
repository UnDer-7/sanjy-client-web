export const ErrorType = Object.freeze({
  JS_ERROR: 'JS_ERROR',
  API_ERROR: 'API_ERROR',
  UNHANDLED_REJECTION: 'UNHANDLED_REJECTION',
  REACT_ERROR: 'REACT_ERROR',
});

export type ErrorType = (typeof ErrorType)[keyof typeof ErrorType];

export interface ErrorLogEntry {
  message: string;
  timestamp: string;
  type: ErrorType;
  pageUrl: string;
  detail: string;
}
