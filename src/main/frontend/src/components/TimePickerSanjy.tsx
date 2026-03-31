import {TimePicker, type TimePickerProps} from "@mantine/dates";
import {getFromLocalStorage} from "../hooks/useCustomLocalStorage.ts";

export function TimePickerSanjy(props: Readonly<TimePickerProps>) {
    const {
        settings: {userTimeFormat},
    } = getFromLocalStorage();

    return <TimePicker
        clearable
        withDropdown
        format={userTimeFormat}
        {...props}
    />;
}