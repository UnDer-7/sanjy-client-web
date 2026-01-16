import {format, type DateArg } from 'date-fns';
import type {TimeFormat} from "../models/CustomTypes.ts";
import {getFromLocalStorage} from "../hooks/useCustomLocalStorage.ts";
import {formatInTimeZone} from 'date-fns-tz';
import {DateTimeFormatPatternService} from "./DateTimeFormatPatternService.ts";

function formatDateIso(date: DateArg<Date>): string {
    return format(date, 'yyyy-MM-dd');
}

function formatTimeIso(time: DateArg<Date>): string {
    if (typeof time === 'string') {
        const parts = time.split(':');
        if (parts.length === 3) {
            return time;
        }
        if (parts.length === 2) {
            return `${time}:00`;
        }
        return time;
    }
    return format(time, 'HH:mm:ss');
}

function formatTimeForDisplay(time: string | Date, timeFormat: TimeFormat, withSeconds = false): string {
    let date: Date;

    // Convert string time to Date object
    if (typeof time === 'string') {
        // Handle HH:mm or HH:mm:ss format
        const timeParts = time.split(':');
        const hours = Number.parseInt(timeParts[0], 10);
        const minutes = Number.parseInt(timeParts[1], 10);
        const seconds = timeParts.length == 3 ? Number.parseInt(timeParts[2], 10) : 0
        date = new Date();
        date.setHours(hours, minutes, seconds, 0);
    } else {
        date = time;
    }

    // Format based on preference
    return format(date, DateTimeFormatPatternService.getTimeFormat(timeFormat, withSeconds))
}

function formatDateForDisplay(date: DateArg<Date>): string {
    const dateFormatPattern = DateTimeFormatPatternService.getDateFormat().dateFns

    let dateObj: Date;
    if (typeof date === 'string') {
        // Parse date string as local date to avoid timezone issues
        // new Date("2026-03-17") interprets as UTC midnight, causing off-by-one day in negative UTC offsets
        // Handle both 'yyyy-MM-dd' and 'yyyy-MM-ddTHH:mm:ss' formats
        const datePart = date.split('T')[0];
        const [year, month, day] = datePart.split('-').map(Number);
        dateObj = new Date(year, month - 1, day); // month is 0-indexed in JS Date
    } else {
        dateObj = date as Date;
    }

    return format(dateObj, dateFormatPattern);
}

function formatDateTimeForDisplay(date: Date, timeFormat: TimeFormat, withSeconds = false): string {
    const dateFormatPattern = DateTimeFormatPatternService.getDateFormat().dateFns

    const timeFormatPattern = DateTimeFormatPatternService.getTimeFormat(timeFormat, withSeconds);

    return format(date, `${dateFormatPattern} ${timeFormatPattern}`);
}

// Format date for backend: 2026-01-08T14:25:00-03:00[America/Sao_Paulo]
function formatDateTimeForBackend(date: Date): string {
    const {settings: { userTimezone }} = getFromLocalStorage();
    const formatted = formatInTimeZone(date, userTimezone, "yyyy-MM-dd'T'HH:mm:ssXXX");
    return `${formatted}[${userTimezone}]`;
}

export const DateTimeService = {
    formatTimeIso,
    formatTimeForDisplay,
    formatDateIso,
    formatDateForDisplay,
    formatDateTimeForDisplay,
    formatDateTimeForBackend,
}