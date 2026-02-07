import {
  Button,
  Center,
  Container,
  Grid,
  Group,
  Loader,
  Paper,
  Select,
  Stack,
  Text,
  Title,
} from '@mantine/core';
import { DataTable } from 'mantine-datatable';
import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router';
import { endOfDay, format, isValid, startOfDay, toDate } from 'date-fns';
import { toZonedTime } from 'date-fns-tz';
import { MealRecordClient } from '../clients/MealRecordClient';
import { useCustomLocalStorage } from '../hooks/useCustomLocalStorage';
import type { MealRecordPageResponse } from '../models/MealRecordPageResponse.ts';
import type { MealRecordSearchResult } from '../models/MealRecord';
import type { SearchMealRecordRequest } from '../models/SearchMealRecordRequest';
import { DateTimeService } from '../services/DateTimeService.ts';
import { DateTimePickerSanjy } from '../components/DateTimePickerSanjy.tsx';

const PAGE_SIZES: number[] = [10, 20, 50, 100];

const PAGE_REQUEST_PARAM_NAMES: {
  readonly consumedAtAfter: string;
  readonly consumedAtBefore: string;
  readonly isFreeMeal: string;
  readonly pageNumber: string;
  readonly pageSize: string;
} = Object.freeze({
  consumedAtAfter: 'consumedAtAfter',
  consumedAtBefore: 'consumedAtBefore',
  isFreeMeal: 'isFreeMeal',
  pageNumber: 'pageNumber',
  pageSize: 'pageSize',
});

export function MealPage() {
  const navigate = useNavigate();
  const {
    settings: { userTimezone, userTimeFormat },
  } = useCustomLocalStorage();
  const [searchParams, setSearchParams] = useSearchParams();

  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<MealRecordPageResponse | null>(null);

  // Filter states
  const [consumedAtAfter, setConsumedAtAfter] = useState<Date | null>(null);
  const [consumedAtBefore, setConsumedAtBefore] = useState<Date | null>(null);
  const [isFreeMeal, setIsFreeMeal] = useState<boolean | null>(null);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Track if this is the initial load
  const [initialized, setInitialized] = useState(false);

  // Initialize filters from URL or defaults
  useEffect(() => {
    const currentDate = toZonedTime(new Date(), userTimezone.value);

    function safelyGetConsumedAtAfterFromParams(): Date {
      const consumedAtAfterParam = searchParams.get(PAGE_REQUEST_PARAM_NAMES.consumedAtAfter);
      if (consumedAtAfterParam) {
        if (isValid(consumedAtAfterParam)) {
          throw new Error('Invalid date');
        }
        return toDate(consumedAtAfterParam);
      } else {
        return startOfDay(currentDate);
      }
    }

    function safelyGetConsumedAtBeforeFromParam(): Date {
      const consumedAtBeforeParam = searchParams.get(PAGE_REQUEST_PARAM_NAMES.consumedAtBefore);
      if (consumedAtBeforeParam) {
        if (isValid(consumedAtBeforeParam)) {
          throw new Error('Invalid date');
        }
        return toDate(consumedAtBeforeParam);
      } else {
        return endOfDay(currentDate);
      }
    }

    function safelyGetNumber(value: string | null, defaultValue: number): number {
      if (!value) {
        return defaultValue;
      }
      if (!Number.isNaN(value.trim())) {
        return Number.parseInt(value);
      }
      throw new Error(`${value} is not a number`);
    }

    function safelyGetIsFreeMealParam(): boolean | null {
      const paramIsFreeMeal = searchParams.get(PAGE_REQUEST_PARAM_NAMES.isFreeMeal);
      if (!paramIsFreeMeal) {
        return null;
      }

      const trimmedValue = paramIsFreeMeal.trim().toLowerCase();
      if (trimmedValue === 'true') {
        return true;
      } else if (trimmedValue === 'false') {
        return false;
      }

      throw new Error(`${paramIsFreeMeal} is not a boolean`);
    }

    const consumedAtAfterParam = safelyGetConsumedAtAfterFromParams();
    setConsumedAtAfter(consumedAtAfterParam);

    const consumedAtBeforeParam = safelyGetConsumedAtBeforeFromParam();
    setConsumedAtBefore(consumedAtBeforeParam);

    const isFreeMealParam = safelyGetIsFreeMealParam();
    setIsFreeMeal(isFreeMealParam);

    const pageNumberParam = safelyGetNumber(
      searchParams.get(PAGE_REQUEST_PARAM_NAMES.pageNumber),
      0
    );
    setPageNumber(pageNumberParam);

    const pageSizeParam = safelyGetNumber(searchParams.get(PAGE_REQUEST_PARAM_NAMES.pageSize), 20);
    setPageSize(pageSizeParam);

    updateQueryParams({
      consumedAtAfter: consumedAtAfterParam,
      consumedAtBefore: consumedAtBeforeParam,
      isFreeMeal: isFreeMealParam,
      pageNumber: pageNumberParam,
      pageSize: pageSizeParam,
    });

    setInitialized(true);
  }, [userTimezone.value]);

  // Fetch data on initial load and when filters/pagination change
  useEffect(() => {
    if (initialized && consumedAtAfter && consumedAtBefore) {
      fetchData();
    }
  }, [initialized, pageNumber, pageSize]);

  // Update URL query params
  const updateQueryParams = (params: {
    consumedAtAfter: Date;
    consumedAtBefore: Date;
    isFreeMeal: boolean | null;
    pageNumber: number;
    pageSize: number;
  }) => {
    const newParams = new URLSearchParams();
    newParams.set(
      PAGE_REQUEST_PARAM_NAMES.consumedAtAfter,
      format(params.consumedAtAfter, "yyyy-MM-dd'T'HH:mm:ss")
    );
    newParams.set(
      PAGE_REQUEST_PARAM_NAMES.consumedAtBefore,
      format(params.consumedAtBefore, "yyyy-MM-dd'T'HH:mm:ss")
    );
    if (params.isFreeMeal !== null) {
      newParams.set(PAGE_REQUEST_PARAM_NAMES.isFreeMeal, params.isFreeMeal.toString());
    }
    newParams.set(PAGE_REQUEST_PARAM_NAMES.pageNumber, params.pageNumber.toString());
    newParams.set(PAGE_REQUEST_PARAM_NAMES.pageSize, params.pageSize.toString());
    setSearchParams(newParams);
  };

  const fetchData = async () => {
    if (!consumedAtAfter || !consumedAtBefore) {
      throw new Error('Dates are null or undefined');
    }

    setLoading(true);
    try {
      const request: SearchMealRecordRequest = {
        consumedAtAfter: consumedAtAfter,
        consumedAtBefore: consumedAtBefore,
        isFreeMeal,
        pageNumber,
        pageSize,
      };

      const response = await MealRecordClient.search(request);
      setData(response);
    } catch (error) {
      console.error('Error fetching meal records:', error);
      setData(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (!consumedAtAfter || !consumedAtBefore) return;

    // Reset to first page when searching
    const newPageNumber = 0;
    setPageNumber(newPageNumber);

    // Update URL
    updateQueryParams({
      consumedAtAfter,
      consumedAtBefore,
      isFreeMeal,
      pageNumber: newPageNumber,
      pageSize,
    });

    // Fetch data
    fetchData();
  };

  // Convert isFreeMeal state (boolean | null) to select value (string)
  const getMealTypeValue = (): string => {
    if (isFreeMeal === null) return 'all';
    return isFreeMeal.toString();
  };

  // Convert select value (string) to isFreeMeal state (boolean | null)
  const setMealTypeFromValue = (value: string | null) => {
    if (value === 'all' || value === null) {
      setIsFreeMeal(null);
    } else if (value === 'true') {
      setIsFreeMeal(true);
    } else if (value === 'false') {
      setIsFreeMeal(false);
    }
  };

  return (
    <Container size="xl" py="xl">
      <Group justify="space-between" align="center" mb="xl">
        <Title order={1}>Meal Records</Title>
        <Button onClick={() => navigate('/meal/new')}>+ New Meal Record</Button>
      </Group>

      {/* Summary Statistics */}
      {data?.mealRecordStatistics && (
        <Paper p="md" mb="xl" withBorder>
          <Group justify="space-around">
            <div>
              <Text size="sm" c="dimmed" ta="center">
                Free Meals
              </Text>
              <Text size="xl" fw={700} ta="center">
                {data.mealRecordStatistics.freeMealQuantity}
              </Text>
            </div>
            <div>
              <Text size="sm" c="dimmed" ta="center">
                Planned Meals
              </Text>
              <Text size="xl" fw={700} ta="center">
                {data.mealRecordStatistics.plannedMealQuantity}
              </Text>
            </div>
            <div>
              <Text size="sm" c="dimmed" ta="center">
                Total Meals
              </Text>
              <Text size="xl" fw={700} ta="center">
                {data.mealRecordStatistics.mealQuantity}
              </Text>
            </div>
          </Group>
        </Paper>
      )}

      {/* Filters */}
      <Paper p="md" mb="xl" withBorder>
        <Stack gap="md">
          <Grid>
            <Grid.Col span={{ base: 12, sm: 6, md: 4 }}>
              <DateTimePickerSanjy
                label="Consumed After"
                placeholder="Select date and time"
                value={consumedAtAfter}
                onChange={(value) => setConsumedAtAfter(value as Date | null)}
                clearable
              />
            </Grid.Col>
            <Grid.Col span={{ base: 12, sm: 6, md: 4 }}>
              <DateTimePickerSanjy
                label="Consumed Before"
                placeholder="Select date and time"
                value={consumedAtBefore}
                onChange={(value) => setConsumedAtBefore(value as Date | null)}
                clearable
              />
            </Grid.Col>
            <Grid.Col span={{ base: 12, sm: 6, md: 4 }}>
              <Select
                label="Meal Type"
                placeholder="Select type"
                value={getMealTypeValue()}
                onChange={setMealTypeFromValue}
                data={[
                  { value: 'all', label: 'All Meals' },
                  { value: 'true', label: 'Free Meals Only' },
                  { value: 'false', label: 'Planned Meals Only' },
                ]}
                clearable
              />
            </Grid.Col>
          </Grid>
          <Button onClick={handleSearch} disabled={!consumedAtAfter || !consumedAtBefore}>
            Search
          </Button>
        </Stack>
      </Paper>

      {/* Data Table */}
      {loading ? (
        <Center py="xl">
          <Loader size="lg" />
        </Center>
      ) : (
        <Paper withBorder>
          <DataTable
            records={data?.page.content || []}
            columns={[
              {
                accessor: 'consumedAt',
                title: 'Consumed At',
                render: (record: MealRecordSearchResult) =>
                  DateTimeService.formatDateTimeForDisplay(
                    toZonedTime(record.consumedAt, userTimezone.value),
                    userTimeFormat.value
                  ),
              },
              {
                accessor: 'isFreeMeal',
                title: 'Free Meal',
                render: (record: MealRecordSearchResult) => (record.isFreeMeal ? 'Yes' : 'No'),
              },
              {
                accessor: 'description',
                title: 'Description',
                render: (record: MealRecordSearchResult) =>
                  record.isFreeMeal
                    ? record.freeMealDescription || '-'
                    : record.standardOption?.description || '-',
              },
              {
                accessor: 'quantity',
                title: 'Quantity & Unit',
                render: (record: MealRecordSearchResult) => `${record.quantity} ${record.unit}`,
              },
            ]}
            totalRecords={data?.page.totalItems || 0}
            recordsPerPage={pageSize}
            page={pageNumber + 1} // DataTable uses 1-based indexing
            onPageChange={(page) => {
              const newPageNumber = page - 1; // Convert to 0-based
              setPageNumber(newPageNumber);
              if (consumedAtAfter && consumedAtBefore) {
                updateQueryParams({
                  consumedAtAfter,
                  consumedAtBefore,
                  isFreeMeal,
                  pageNumber: newPageNumber,
                  pageSize,
                });
              }
            }}
            recordsPerPageOptions={PAGE_SIZES}
            onRecordsPerPageChange={(newPageSize) => {
              setPageSize(newPageSize);
              setPageNumber(0); // Reset to first page
              if (consumedAtAfter && consumedAtBefore) {
                updateQueryParams({
                  consumedAtAfter,
                  consumedAtBefore,
                  isFreeMeal,
                  pageNumber: 0,
                  pageSize: newPageSize,
                });
              }
            }}
            minHeight={200}
            noRecordsText="No meal records found"
          />
        </Paper>
      )}
    </Container>
  );
}
