import {DateTimePicker, type DateTimePickerProps} from '@mantine/dates'
import {useCustomLocalStorage} from "../hooks/useCustomLocalStorage.ts";

export function DateTimePickerSanjy(props: Readonly<DateTimePickerProps>) {
    const { settings: { userTimeFormat: { value: timeFormatValue }}} = useCustomLocalStorage();

    const dateFormat = navigator.language === 'en-US' ?
        "MMM DD, YYYY" :
        "DD MMM, YYYY";

    const timeFormat = timeFormatValue === '24h' ?
        "HH:mm" :
        "hh:mm A"

    return (
        <DateTimePicker
            {...props}
            valueFormat={`${dateFormat} ${timeFormat}`}
        />
    );
}