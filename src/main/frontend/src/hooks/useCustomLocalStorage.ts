import {useLocalStorage} from "@mantine/hooks";

const localStorageKeys = {
    userTimezone: 'USER_TIMEZONE',
}

export function useCustomLocalStorage() {
    const [valueTimezone, setValueTimezone] = useLocalStorage<string>({
        key: localStorageKeys.userTimezone,
        defaultValue: Intl.DateTimeFormat().resolvedOptions().timeZone
    });

    return {
        settings: {
            userTimezone: { value: valueTimezone, setValue: setValueTimezone },
        }
    }
}