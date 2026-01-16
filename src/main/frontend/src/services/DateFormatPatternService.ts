type getDateFormatTypeReturn = {
    dayjs: string
    dateFns: string
};

function getFormat(): getDateFormatTypeReturn {
    // https://en.wikipedia.org/wiki/List_of_date_formats_by_country

    const formatToParts = new Intl.DateTimeFormat(navigator.language)
        .formatToParts(new Date());

    const yearIndex = formatToParts.findIndex(p => p.type === 'year');
    const monthIndex = formatToParts.findIndex(p => p.type === 'month');
    const dayIndex = formatToParts.findIndex(p => p.type === 'day');

    if (yearIndex < monthIndex) {
        // YMD - Year/Month/Day (China, Japan, South Korea, Taiwan, Hungary, Mongolia, Lithuania, Bhutan)
        return {
            dayjs: "YYYY, MMM DD",
            dateFns: 'yyyy, MMM dd'
        };
    }

    if (monthIndex < dayIndex) {
        // MDY - Month/Day/Year (United States, Kenya, Canada, Ghana)
        return {
            dayjs: "MMM DD, YYYY",
            dateFns: 'MMM dd, yyyy'
        };
    }

    // DMY - Day/Month/Year (Rest of the world)
    return {
        dayjs: "DD MMM, YYYY",
        dateFns: 'dd MMM, yyyy'
    };
}

export const DateFormatPatternService = {
    getFormat,
}