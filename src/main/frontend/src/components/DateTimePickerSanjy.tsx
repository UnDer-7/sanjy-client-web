import {DateTimePicker, type DateTimePickerProps} from '@mantine/dates'
import {getFromLocalStorage} from "../hooks/useCustomLocalStorage.ts";
import {DateFormatPatternService} from "../services/DateFormatPatternService.ts";

export function DateTimePickerSanjy(props: Readonly<DateTimePickerProps>) {
    const { settings: { userTimeFormat }} = getFromLocalStorage();

    const dateFormat = DateFormatPatternService.getFormat().dayjs;

    const timeFormat = userTimeFormat === '24h' ?
        "HH:mm" :
        "hh:mm A"

    return (
        <DateTimePicker
            {...props}
            valueFormat={`${dateFormat} ${timeFormat}`}
        />
    );
}
