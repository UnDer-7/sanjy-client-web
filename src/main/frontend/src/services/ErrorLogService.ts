import { type ErrorLogEntry, ErrorType } from '../models/ErrorLog';

const LOCAL_STORAGE_KEY = 'sanjy_error_logs';
const MAX_ERROR_ENTRIES = 66;

function getErrorLogs(): ErrorLogEntry[] {
    try {
        const stored = localStorage.getItem(LOCAL_STORAGE_KEY);
        if (!stored) {
            return [];
        }
        return JSON.parse(stored) as ErrorLogEntry[];
    } catch {
        return [];
    }
}

function saveErrorLogs(logs: ErrorLogEntry[]): void {
    try {
        localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(logs));
    } catch {
        // localStorage might be full or unavailable
    }
}

function addErrorLog(entry: ErrorLogEntry): void {
    const logs = getErrorLogs();

    if (logs.length >= MAX_ERROR_ENTRIES) {
        logs.shift(); // Remove oldest entry
    }

    logs.push(entry);
    saveErrorLogs(logs);
}

function createErrorEntry(
    message: string,
    type: ErrorType,
    detail: string
): ErrorLogEntry {
    return {
        message,
        timestamp: new Date().toISOString(),
        type,
        url: window.location.pathname,
        detail
    };
}

export function logJsError(error: Error): void {
    const entry = createErrorEntry(
        error.message,
        ErrorType.JS_ERROR,
        error.stack || ''
    );
    addErrorLog(entry);
}

export function logApiError(message: string, responseBody: unknown): void {
    const detail = typeof responseBody === 'string'
        ? responseBody
        : JSON.stringify(responseBody, null, 2);

    const entry = createErrorEntry(
        message,
        ErrorType.API_ERROR,
        detail
    );
    addErrorLog(entry);
}

export function logUnhandledRejection(reason: unknown): void {
    const message = reason instanceof Error ? reason.message : String(reason);
    const detail = reason instanceof Error ? (reason.stack || '') : String(reason);

    const entry = createErrorEntry(
        message,
        ErrorType.UNHANDLED_REJECTION,
        detail
    );
    addErrorLog(entry);
}

export function logReactError(error: Error, componentStack: string): void {
    const entry = createErrorEntry(
        error.message,
        ErrorType.REACT_ERROR,
        `Stack: ${error.stack || ''}\n\nComponent Stack: ${componentStack}`
    );
    addErrorLog(entry);
}

export function clearErrorLogs(): void {
    localStorage.removeItem(LOCAL_STORAGE_KEY);
}

export function getStoredErrorLogs(): ErrorLogEntry[] {
    return getErrorLogs();
}

export const errorLogService = {
    logJsError,
    logApiError,
    logUnhandledRejection,
    logReactError,
    clearErrorLogs,
    getStoredErrorLogs
};
