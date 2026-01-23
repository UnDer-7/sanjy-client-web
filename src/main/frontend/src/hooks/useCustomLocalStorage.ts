import {useLocalStorage} from "@mantine/hooks";
import type {TimeFormat} from "../models/CustomTypes.ts";
import type {ErrorLogEntry} from "../models/ErrorLog.ts";
import {defaultTo} from "ramda";

export type LocalStorageAction<T> = {
    value: T,
    setValue: (value: T) => void;
}

export type UseCustomLocalStorageType = {
    settings: {
        userTimezone: LocalStorageAction<string>,
        userTimeFormat: LocalStorageAction<TimeFormat>,
    },
    errorLogs: LocalStorageAction<ErrorLogEntry[]>,
};

export const localStorageKeys = {
    userTimezone: 'USER_TIMEZONE',
    userTimeFormat: 'USER_TIME_FORMAT',
    errorLogs: 'SANJY_ERROR_LOGS',
} as const;

// Detect user's preferred time format based on locale
function getDefaultTimeFormat(): TimeFormat {
    try {
        const locale = navigator.language || 'en-US';
        const testDate = new Date(2000, 0, 1, 13, 0, 0);
        const formatted = new Intl.DateTimeFormat(locale, {
            hour: 'numeric',
            minute: 'numeric',
        }).format(testDate);

        // Check if the formatted time contains AM/PM indicators
        const hasAmPm = /am|pm/i.test(formatted);
        return hasAmPm ? '12h' : '24h';
    } catch {
        return '24h'; // Default to 24h if detection fails
    }
}

function getDefaultTimezone(): string {
    return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

export function useCustomLocalStorage(): UseCustomLocalStorageType {
    const [valueTimezone, setValueTimezone] = useLocalStorage<string>({
        key: localStorageKeys.userTimezone,
        defaultValue: getDefaultTimezone()
    });

    const [valueTimeFormat, setValueTimeFormat] = useLocalStorage<TimeFormat>({
        key: localStorageKeys.userTimeFormat,
        defaultValue: getDefaultTimeFormat(),
    });

    const [valueErrorLogs, setValueErrorLogs] = useLocalStorage<ErrorLogEntry[]>({
        key: localStorageKeys.errorLogs,
        defaultValue: [],
    });

    return {
        settings: {
            userTimezone: { value: valueTimezone, setValue: setValueTimezone },
            userTimeFormat: { value: valueTimeFormat, setValue: setValueTimeFormat },
        },
        errorLogs: { value: valueErrorLogs, setValue: setValueErrorLogs },
    }
}

export interface GetFromLocalStorage {
    settings: {
        userTimezone: string;
        userTimeFormat: TimeFormat;
    },
    errorLogs: ErrorLogEntry[];
}

export function getFromLocalStorage(): GetFromLocalStorage {
    const userTimezone = localStorage.getItem(localStorageKeys.userTimezone);
    const userTimeFormat = localStorage.getItem(localStorageKeys.userTimeFormat);
    const errorLogs = localStorage.getItem(localStorageKeys.errorLogs);

    let parsedErrorLogs: ErrorLogEntry[] = [];
    try {
        parsedErrorLogs = errorLogs ? JSON.parse(errorLogs) : [];
    } catch {
        parsedErrorLogs = [];
    }

    return {
        settings: {
            userTimezone: defaultTo(getDefaultTimezone(), userTimezone).replaceAll('"', ''),
            userTimeFormat: defaultTo(getDefaultTimeFormat(), userTimeFormat).replaceAll('"', '') as TimeFormat,
        },
        errorLogs: parsedErrorLogs,
    }
}