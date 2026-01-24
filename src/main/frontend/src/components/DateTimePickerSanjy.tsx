import { DateTimePicker, type DateTimePickerProps } from '@mantine/dates';
import { getFromLocalStorage } from '../hooks/useCustomLocalStorage.ts';
import { DateTimeFormatPatternService } from '../services/DateTimeFormatPatternService.ts';

export function DateTimePickerSanjy(props: Readonly<DateTimePickerProps>) {
  const {
    settings: { userTimeFormat },
  } = getFromLocalStorage();

  const dateFormat = DateTimeFormatPatternService.getDateFormat().dayjs;
  const timeFormat = DateTimeFormatPatternService.getTimeFormat(userTimeFormat);

  return <DateTimePicker {...props} valueFormat={`${dateFormat} ${timeFormat}`} />;
}
