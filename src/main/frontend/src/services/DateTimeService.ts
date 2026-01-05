import {format, type DateArg } from 'date-fns';

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

export const DateTimeService = {
    formateDate,
    formatTime,
}