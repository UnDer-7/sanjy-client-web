import {format, type DateArg } from 'date-fns';
import type {TimeFormat} from "../models/CustomTypes.ts";

function formateDate(date: DateArg<Date>): string {
    return format(date, 'yyyy-MM-dd');
}

function formatTime(time: DateArg<Date>): string {
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

// Format time for display based on user preference (12h or 24h)
function formatTimeForDisplay(time: string | Date, timeFormat: TimeFormat): string {
    let date: Date;

    // Convert string time to Date object
    if (typeof time === 'string') {
        // Handle HH:mm or HH:mm:ss format
        const timeParts = time.split(':');
        const hours = Number.parseInt(timeParts[0], 10);
        const minutes = Number.parseInt(timeParts[1], 10);
        date = new Date();
        date.setHours(hours, minutes, 0, 0);
    } else {
        date = time;
    }

    // Format based on preference
    if (timeFormat === '12h') {
        return format(date, 'hh:mm a'); // e.g., "09:30 AM"
    } else {
        return format(date, 'HH:mm'); // e.g., "09:30"
    }
}

// Format datetime for display based on user preference
function formatDateTimeForDisplay(date: Date, timeFormat: TimeFormat): string {
    if (timeFormat === '12h') {
        return format(date, 'MMM dd, yyyy hh:mm:ss a'); // e.g., "Jan 07, 2026 09:30:45 PM"
    } else {
        return format(date, 'MMM dd, yyyy HH:mm:ss'); // e.g., "Jan 07, 2026 21:30:45"
    }
}

export const DateTimeService = {
    formateDate,
    formatTime,
    formatTimeForDisplay,
    formatDateTimeForDisplay,
}